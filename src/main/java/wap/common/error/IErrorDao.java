package wap.common.error;


import wap.common.ServiceFault;

/**
 * Created by haoho on 10/17/18 16:34.
 */
public interface IErrorDao {
  String registerBackEndFault(ServiceFault fault);

  String registerBackEndFault(ServiceFault fault, StackTraceElement[] stack);

  String registerBackEndFault(ServiceFault fault, StackTraceElement[] stack, String dump);

}

