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
         * URI无效，应该跳过处理,直接结束
         */
        FINISH,

        EXTRACT,
        WRITER,
        POSTLINK,
    }
    
    final public static ProcessResult PROCEED = 
        new ProcessResult(ProcessStatus.PROCEED);
    
    final public static ProcessResult FINISH =
        new ProcessResult(ProcessStatus.FINISH);
    final public static ProcessResult EXTRACT =
            new ProcessResult(ProcessStatus.EXTRACT);
    final public static ProcessResult WRITER =
            new ProcessResult(ProcessStatus.WRITER);
    final public static ProcessResult POSTLINK =
            new ProcessResult(ProcessStatus.POSTLINK);
    
    final private ProcessStatus status;
    
   
    private ProcessResult(ProcessStatus status) {
    	 this.status = status;
    }
}
