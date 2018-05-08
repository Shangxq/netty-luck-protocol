package protocol;

public class LuckHeader {

    /**
     * 消息开头信息
     */
    private int headerData = ConstantValue.HEAD_DATA;

    /**
     * 消息体长度
     */
    private int contentLength;

//    /**
//     * 消息会话id
//     */
//    private String sessionId;

    /**
     * 文件名长度
     */
    private byte nameLength;

    /**
     * 文件名
     */
    private String fileName;

    /**
     *分片信息（片数，第几片）
     */

    public int getHeaderData() {
        return headerData;
    }

    public void setHeaderData(final int headerData) {
        this.headerData = headerData;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public void setNameLength(final byte nameLength) {
        this.nameLength = nameLength;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }
//
//    public String getSessionId() {
//        return sessionId;
//    }
//
//    public void setSessionId(final String sessionId) {
//        this.sessionId = sessionId;
//    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public LuckHeader(final int contentLength) {
        this.contentLength = contentLength;
//        this.sessionId = sessionId;
    }

    public LuckHeader(final int contentLength, final byte nameLength, final String fileName) {
        this.nameLength = nameLength;
        this.fileName = fileName;
        this.contentLength = contentLength;
//        this.sessionId = sessionId;
    }

    public LuckHeader() {
    }

    @Override
    public String toString() {
        return "LuckHeader{" +
                "headerData=" + headerData +
                ", nameLength=" + nameLength +
                ", fileName='" + fileName + '\'' +
                ", contentLength=" + contentLength +
//                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
