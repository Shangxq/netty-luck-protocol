package protocol;

public class LuckMessage {
    private LuckHeader luckHeader;
    private byte[] content;

    public LuckMessage(final LuckHeader luckHeader, final byte[] content) {
        this.luckHeader = luckHeader;
        this.content = content;
    }

    public LuckHeader getLuckHeader() {
        return luckHeader;
    }

    public void setLuckHeader(final LuckHeader luckHeader) {
        this.luckHeader = luckHeader;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LuckMessage{" +
                "luckHeader=" + luckHeader.toString() +
                ", content=" + new String(content) +
                '}';
    }
}
