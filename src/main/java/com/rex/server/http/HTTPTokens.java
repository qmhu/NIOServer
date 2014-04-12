package com.rex.server.http;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 3/31/14
 * Time: 8:55 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * HTTP constants
 */
public interface HTTPTokens
{
    // Terminal symbols.
    static final byte COLON= (byte)':';
    static final byte SPACE= 0x20;
    static final byte CARRIAGE_RETURN= 0x0D;
    static final byte LINE_FEED= 0x0A;
    static final byte[] CRLF = {CARRIAGE_RETURN,LINE_FEED};
    static final byte SEMI_COLON= (byte)';';
    static final byte TAB= 0x09;
}