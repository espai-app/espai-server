/*
 * Copyright 2023 Saxon State and University Library Dresden (SLUB)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.espai.views;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

/**
 *
 * @author borowski
 */
@WebListener
public class ApplicationConfigListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        
        // By default JSF doesn't preserve the query string when creating action URLs for
        // forms. To avoid complex workarounds (e.g. by adding hidden fields) we changed
        // the ViewHandler to add the query string to the action URL.
        
        // add a view handler to add GET parameters to actionUrls
        if (FacesContext.getCurrentInstance() != null) {
            Application app = FacesContext.getCurrentInstance().getApplication();
            app.setViewHandler(new KeepGetParametersViewHandler(app.getViewHandler()));
        }
    }

}
