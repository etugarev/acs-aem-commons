/*
 * #%L
 * ACS AEM Commons Package
 * %%
 * Copyright (C) 2016 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package apps.acs_commons.components.dam.fonts;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Fonts implements Use {

    private static final Logger log = LoggerFactory.getLogger(Fonts.class);

    private Map<String, FontFamily> fonts;

    @Override
    public void init(Bindings bindings) {
        SlingHttpServletRequest request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
        String itemPath = request.getParameter("item");

        if (itemPath != null) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource metadataResource = resourceResolver.getResource(itemPath + "/jcr:content/metadata");

            Resource fontsResource = metadataResource.getChild("xmpTPg:Fonts");
            if (fontsResource != null) {
                this.fonts = new TreeMap<String, FontFamily>();
                for (Resource fontResource : fontsResource.getChildren()) {
                    ValueMap properties = fontResource.getValueMap();
                    String fontFamily = properties.get("stFNT:fontFamily", String.class);
                    String fontFace = properties.get("stFNT:fontFace", String.class);
                    if (fontFace != null && fontFamily != null) {
                        FontFamily family = this.fonts.get(fontFamily);
                        if (family == null) {
                            family = new FontFamily();
                            this.fonts.put(fontFamily, family);
                        }
                        family.faces.add(fontFace);
                    }
                }
            }
        }

    }

    public Map<String, FontFamily> getFonts() {
        return fonts;
    }

    public boolean getHasContent() {
        return fonts != null && fonts.size() > 0;
    }

    public class FontFamily {
        public Collection<String> faces = new TreeSet<String>();

        public boolean getHasMultiple() {
            return faces.size() > 1;
        }
    }

}