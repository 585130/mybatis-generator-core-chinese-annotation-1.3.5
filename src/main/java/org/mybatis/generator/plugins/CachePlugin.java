/**
 * Copyright 2006-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * This plugin adds a cache element to generated sqlMaps.  This plugin
 * is for MyBatis3 targeted runtimes only.  The plugin accepts the
 * following properties (all are optional):
 *
 * cache_eviction
 * cache_flushInterval
 * cache_size
 * cache_readOnly
 * cache_type
 *
 * All properties correspond to properties of the MyBatis cache element and
 * are passed "as is" to the corresponding properties of the generated cache
 * element.  All properties can be specified at the table level, or on the
 * plugin element.  The property on the table element will override any
 * property on the plugin element.
 *
 * 这个插件是一个挺有用的插件，用来生成在XML中的<cache>元素（这个插件只针对MyBatis3/MyBatis3Simple有效哈）；
 * 很显然，这个插件需要一些配置，支持的配置属性有：cache_eviction，cache_flushInterval，cache_readOnly，cache_size，cache_type，具体就不解释了，和cache元素的属性一一对应；
 * 很好的一点，在<table>元素中，可以通过定义property元素，来覆盖<plugin>元素中提供的默认值；
 * @author Jason Bennett
 * @author Jeff Butler
 */
public class CachePlugin extends PluginAdapter {
    public CachePlugin() {
        super();
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement element = new XmlElement("cache");
        context.getCommentGenerator().addComment(element);

        for (CacheProperty cacheProperty : CacheProperty.values()) {
            addAttributeIfExists(element, introspectedTable, cacheProperty);
        }

        document.getRootElement().addElement(element);

        return true;
    }

    private void addAttributeIfExists(XmlElement element, IntrospectedTable introspectedTable,
                                      CacheProperty cacheProperty) {
        String property = introspectedTable.getTableConfigurationProperty(cacheProperty.getPropertyName());
        if (property == null) {
            property = properties.getProperty(cacheProperty.getPropertyName());
        }

        if (StringUtility.stringHasValue(property)) {
            element.addAttribute(new Attribute(cacheProperty.getAttributeName(), property));
        }
    }

    public enum CacheProperty {
        EVICTION("cache_eviction", "eviction"),  //$NON-NLS-2$
        FLUSH_INTERVAL("cache_flushInterval", "flushInterval"),  //$NON-NLS-2$
        READ_ONLY("cache_readOnly", "readOnly"),  //$NON-NLS-2$
        SIZE("cache_size", "size"),  //$NON-NLS-2$
        TYPE("cache_type", "type");  //$NON-NLS-2$

        private String propertyName;
        private String attributeName;

        CacheProperty(String propertyName, String attributeName) {
            this.propertyName = propertyName;
            this.attributeName = attributeName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getAttributeName() {
            return attributeName;
        }
    }
}
