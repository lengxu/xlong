package uyun.xianglong.sdk.node;

/**
 * @author 游龙
 * @Description
 * @date 2017-10-24 14:12
 */
public class PipelineNodeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PipelineNodeException(String msg) {
        super(msg);
    }

    public PipelineNodeException(String msg, Throwable th) {
        super(msg, th);
    }

    public PipelineNodeException(Throwable th) {
        super(th);
    }

}
