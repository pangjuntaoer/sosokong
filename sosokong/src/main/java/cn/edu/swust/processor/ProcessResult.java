package cn.edu.swust.processor;
/**
 * 2014年10月07日18:52:23
 * @author pery
 *
 */
public class ProcessResult {
    public enum ProcessStatus {
        /**
         * URI正常处理
         */
        PROCEED,

        /**
         * URI无效，应该跳过处理
         */
        FINISH,

        /**
         * The Processor has specified the next processor for the URI.  The 
         * china should skip forward to that processor instead of the reguarly
         * scheduled next processor.
         */
        JUMP,
    }
    
    final public static ProcessResult PROCEED = 
        new ProcessResult(ProcessStatus.PROCEED);
    
    final public static ProcessResult FINISH =
        new ProcessResult(ProcessStatus.FINISH);
    
    
    final private ProcessStatus status;
    final private String jumpTarget;
    
    
    private ProcessResult(ProcessStatus status) {
        this(status, null);
    }
    
    
    private ProcessResult(ProcessStatus status, String jumpName) {
        this.status = status;
        this.jumpTarget = jumpName;
    }
    
    
    public ProcessStatus getProcessStatus() {
        return status;
    }
    
    
    public String getJumpTarget() {
        return jumpTarget;
    }
    
    
    public static ProcessResult jump(String jumpTarget) {
        return new ProcessResult(ProcessStatus.JUMP, jumpTarget);
    }
}
