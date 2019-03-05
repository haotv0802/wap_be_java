package wap.common.error;

import wap.common.ServiceFault;

/**
 * Created by haoho on 10/17/18 16:34.
 */
public interface IErrorService {
  ServiceFault registerBackEndFault(ServiceFault sf, StackTraceElement[] stack);
}

