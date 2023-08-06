package com.hyc.chatproxy.sip;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.*;

@Slf4j
public class MySipServer implements SipListener {
    private SipFactory sipFactory;
    private SipStack sipStack;
    private SipProvider udpSipProvider;
    private SipProvider tcpSipProvider;

    String ip = "192.168.1.6";
    List<String> sipUsers = new ArrayList<>();

    {
        sipUsers.add("1001");
    }

    public void init() throws Exception {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "mySipServer");
        sipStack = sipFactory.createSipStack(properties);
        ListeningPoint listeningPoint = sipStack.createListeningPoint(ip, 5060, "udp");
        ListeningPoint listeningPoint2 = sipStack.createListeningPoint(ip, 5060, "tcp");
        udpSipProvider = sipStack.createSipProvider(listeningPoint);
        udpSipProvider.addSipListener(this);
        log.info("UDP SIP Server is ready to receive messages.");
        tcpSipProvider = sipStack.createSipProvider(listeningPoint2);
        tcpSipProvider.addSipListener(this);
        log.info("TCP SIP Server is ready to receive messages.");

    }

    // Implement SIP Listener methods for request and response handling
    @SneakyThrows
    public void processRequest(RequestEvent requestEvent) {
        // Handle incoming SIP requests here
        log.info("processRequest:{}", requestEvent);
        // For simplicity, assume the request is an INVITE and we want to send 200 OK response

        String method = requestEvent.getRequest().getMethod();
        ServerTransaction serverTransaction = requestEvent.getServerTransaction();
        if (serverTransaction == null) {
            serverTransaction = udpSipProvider.getNewServerTransaction(requestEvent.getRequest());
        }
        log.info("server,branchid:{},dialog:{},applicationdata:{}", serverTransaction.getBranchId(), serverTransaction.getDialog(), serverTransaction.getApplicationData());

        switch (method) {
            case Request.REGISTER:
                // Set necessary headers in the response (Contact, CSeq, etc.)
                // ...
                // Send the response to the client

                URI requestURI = requestEvent.getRequest().getRequestURI();
                log.info("requestURI:{}", requestURI);

                ListIterator headerNames = requestEvent.getRequest().getHeaderNames();
                String sipUser = "";
                while (headerNames.hasNext()) {
                    String name = headerNames.next().toString();
                    Header header = requestEvent.getRequest().getHeader(name);
                    String value = header.toString();
                    if (name.equalsIgnoreCase("to")) {
                        int start = value.indexOf("sip:") + 4;
                        int end = value.indexOf("@");
                        sipUser = value.substring(start, end);
                        log.info("sipUser:{}", sipUser);
                    }
                }

                Response response;
                if (!sipUsers.contains(sipUser)) {
                    response = sipFactory.createMessageFactory().createResponse(Response.UNAUTHORIZED, requestEvent.getRequest());
                } else {
                    response = sipFactory.createMessageFactory().createResponse(Response.OK, requestEvent.getRequest());
                }

                serverTransaction.sendResponse(response);
                break;

            case Request.INVITE:

                try {
                    //先发送100try
                    Response tryResponse = sipFactory.createMessageFactory().createResponse(Response.TRYING, requestEvent.getRequest());
                    serverTransaction.sendResponse(tryResponse);
                    log.info("try:{}", tryResponse);

                    //等待3秒钟,发送180振铃音
                    Thread.sleep(3000L);
                    Response ringResponse = response = sipFactory.createMessageFactory().createResponse(Response.RINGING, requestEvent.getRequest());
                    serverTransaction.sendResponse(ringResponse);
                    log.info("ring:{}", ringResponse);

                    //等待2秒钟,发送200ok,并增加SDP
                    Response okResponse = sipFactory.createMessageFactory().createResponse(Response.OK, requestEvent.getRequest());
                    // Add SDP to response for Ringing Tone
                    // Set SDP information in 200 OK response if needed
                    String sdpData = "v=0\r\n"
                            + "o=- 123456 0 IN IP4 192.168.1.6\r\n"
                            + "s=JavaSipCall\r\n"
                            + "c=IN IP4 192.168.1.6\r\n"
                            + "t=0 0\r\n"
                            + "m=audio 5004 RTP/AVP 0\r\n"
                            + "a=rtpmap:0 PCMU/8000\r\n";
                    HeaderFactory headerFactory = sipFactory.createHeaderFactory();
                    ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
                    okResponse.setContent(sdpData, contentTypeHeader);

                    AddressFactory addressFactory = sipFactory.createAddressFactory();
                    // Add Contact header to the 200 OK response
                    SipURI contactUri = addressFactory.createSipURI("alice", ip);
                    contactUri.setPort(5060);
                    Address contactAddress = addressFactory.createAddress(contactUri);
                    ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
                    okResponse.addHeader(contactHeader);

                    serverTransaction.sendResponse(okResponse);

                    log.info("ok:{}", okResponse);
                } catch (Exception e) {
                    log.error("handler INVITE error:", e);
                }

            case Request.ACK:
                log.info("method:{},request:{}", requestEvent.getRequest().getMethod(), requestEvent.getRequest());
                break;
        }


    }

    public void processResponse(ResponseEvent responseEvent) {
        // Handle incoming SIP responses here
        log.info("processResponse:{}", responseEvent);
    }

    public void processTimeout(TimeoutEvent timeoutEvent) {
        // Handle timeout events here
        log.info("processTimeout:{}", timeoutEvent);
    }

    // Implement other SIP Listener methods as needed

    public void processIOException(IOExceptionEvent exceptionEvent) {
        // Handle IO exceptions here
        log.info("processIOException:{}", exceptionEvent);
    }

    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // Handle transaction terminated events here
        log.info("processTransactionTerminated:{}", transactionTerminatedEvent);
    }

    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // Handle dialog terminated events here
        log.info("processDialogTerminated:{}", dialogTerminatedEvent);
    }
}