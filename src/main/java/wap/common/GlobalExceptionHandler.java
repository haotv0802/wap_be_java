package wap.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import wap.common.error.IErrorService;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Will make sure to translate some exceptions to a meaningful response to the client.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  private final Logger LOGGER = LogManager.getLogger(getClass());
  private static final int PL_SQL_USER_DEFINED_ERR_CODE_RANGE_END = 20999;
  private static final int PL_SQL_USER_DEFINED_ERR_CODE_RANGE_START = 20000;
  private static final Pattern NEW_LINE = Pattern.compile("\\R");

  private final ResourceBundleMessageSource messageSource;

  // TODO LOGGER error code into database
  private final IErrorService errorService;

  @Autowired
  public GlobalExceptionHandler(
      @Qualifier("messageSource") ResourceBundleMessageSource messageSource,
      @Qualifier("errorService") IErrorService errorService
  ) {
    Assert.notNull(messageSource);
    Assert.notNull(errorService);

    this.messageSource = messageSource;
    this.errorService = errorService;
  }


  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) //400
  @ResponseBody
  public ServiceFault handleConflict(ValidationException e) {
    String faultCode = e.getFaultCode();
    Object[] context = e.getContext();
    LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ERROR");
    LOGGER.error(e.getMessage(), e);
    LOGGER.error("Context: " + (e.getContext() == null ?
        messageSource.getMessage(faultCode, context, LocaleContextHolder.getLocale()) : Arrays.toString(e.getContext())));
    LOGGER.error("ERROR <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

    ServiceFault fault = new ServiceFault(faultCode, messageSource.getMessage(faultCode, context, LocaleContextHolder.getLocale()));
    return errorService.registerBackEndFault(fault, e.getStackTrace());
  }


  @ExceptionHandler(EmptyResultDataAccessException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND) // 404
  public ServiceFault handleConflict(EmptyResultDataAccessException e) {
    LOGGER.info("Info: ", e);
    return errorService.registerBackEndFault(new ServiceFault(HttpStatus.NOT_FOUND.toString(), e.getMessage()), e.getStackTrace());
  }

  @ExceptionHandler(CannotAcquireLockException.class)
  @ResponseStatus(HttpStatus.LOCKED) // 423
  public ServiceFault handleConflict(CannotAcquireLockException e) {
    LOGGER.info("Info:", e);
    return errorService.registerBackEndFault(new ServiceFault(HttpStatus.LOCKED.toString(), e.getMessage()), e.getStackTrace());
  }

}