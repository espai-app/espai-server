package app.espai.views;

import org.primefaces.model.DialogFrameworkOptions;

/**
 *
 * @author rborowski
 */
public class Dialog {

  public static DialogFrameworkOptions getDefaultOptions(int width, int height) {
     DialogFrameworkOptions options = DialogFrameworkOptions.builder()
            .modal(true)
            .width(String.valueOf(width))
            .height(String.valueOf(height))
            .contentHeight("100%")
            .contentWidth("100%")
            .fitViewport(true)
            .blockScroll(true)
            .resizeObserver(true)
            .resizeObserverCenter(true)
            .responsive(true)
            .styleClass("max-w-screen")
            .iframeStyleClass("max-w-screen")
            .build();
     return options;
  }

}
