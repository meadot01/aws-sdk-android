/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests of exception handling
 */
public class ExceptionHandlingIntegrationTest extends DynamoDBMapperIntegrationTestBase {

    public static class NoTableAnnotation {

        private String key;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testNoTableAnnotation() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.save(new NoTableAnnotation());
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testNoTableAnnotationLoad() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(NoTableAnnotation.class, "abc");
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class NoDefaultConstructor {

        private String key;
        private String attribute;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public NoDefaultConstructor(String key, String attribute) {
            super();
            this.key = key;
            this.attribute = attribute;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testNoDefaultConstructor() {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        NoDefaultConstructor obj = new NoDefaultConstructor("" + startKey++, "abc");
        util.save(obj);
        util.load(NoDefaultConstructor.class, obj.getKey());
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class NoKeyGetterDefined {

        @SuppressWarnings("unused")
        private String key;
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testNoHashKeyGetter() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.save(new NoKeyGetterDefined());
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testNoHashKeyGetterLoad() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(NoKeyGetterDefined.class, "abc");
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class PrivateKeyGetter {

        private String key;

        @SuppressWarnings("unused")
        @DynamoDBHashKey
        private String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        private void setKey(String key) {
            this.key = key;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testPrivateKeyGetter() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.save(new PrivateKeyGetter());
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testPrivateKeyGetterLoad() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(PrivateKeyGetter.class, "abc");
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class PrivateKeySetter {

        private String key;

        @DynamoDBHashKey
        @DynamoDBAutoGeneratedKey
        public String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        private void setKey(String key) {
            this.key = key;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testPrivateKeySetter() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.save(new PrivateKeySetter());
    }

    /*
     * To trigger this error, we need for a service object to be present, so
     * we'll insert one manually.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testPrivateKeySetterLoad() throws Exception {
        Map<String, AttributeValue> attr = new HashMap<String, AttributeValue>();
        attr.put(KEY_NAME, new AttributeValue().withS("abc"));
        dynamo.putItem(new PutItemRequest().withTableName("aws-java-sdk-util").withItem(attr));
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(PrivateKeySetter.class, "abc");
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class PrivateSetter {

        private String key;
        private String StringProperty;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getStringProperty() {
            return StringProperty;
        }

        private void setStringProperty(String stringProperty) {
            StringProperty = stringProperty;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testPrivateSetterLoad() throws Exception {
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        PrivateSetter object = new PrivateSetter();
        object.setStringProperty("value");
        util.save(object);
        util.load(PrivateSetter.class, object.getKey());
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class OverloadedSetter {

        private String key;
        private String attribute;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute, String unused) {
            this.attribute = attribute;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testOverloadedSetter() {
        OverloadedSetter obj = new OverloadedSetter();
        obj.setKey("" + startKey++);
        obj.setAttribute("abc", "123");
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
        mapper.save(obj);

        mapper.load(OverloadedSetter.class, obj.getKey());
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class WrongTypeForSetter {

        private String key;
        private String attribute;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(Integer attribute) {
            this.attribute = String.valueOf(attribute);
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testWrongTypeForSetter() {
        WrongTypeForSetter obj = new WrongTypeForSetter();
        obj.setKey("" + startKey++);
        obj.setAttribute(123);
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
        mapper.save(obj);

        mapper.load(WrongTypeForSetter.class, obj.getKey());
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class NumericFields {

        private String key;
        private Integer integerProperty;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getIntegerProperty() {
            return integerProperty;
        }

        public void setIntegerProperty(Integer integerProperty) {
            this.integerProperty = integerProperty;
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testWrongDataType() {
        Map<String, AttributeValue> attr = new HashMap<String, AttributeValue>();
        attr.put("integerProperty", new AttributeValue().withS("abc"));
        attr.put(KEY_NAME, new AttributeValue().withS("" + startKey++));
        dynamo.putItem(new PutItemRequest().withTableName("aws-java-sdk-util").withItem(attr));
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(NumericFields.class, attr.get(KEY_NAME).getS());
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testWrongDataType2() {
        Map<String, AttributeValue> attr = new HashMap<String, AttributeValue>();
        attr.put("integerProperty", new AttributeValue().withNS("1", "2", "3"));
        attr.put(KEY_NAME, new AttributeValue().withS("" + startKey++));
        dynamo.putItem(new PutItemRequest().withTableName("aws-java-sdk-util").withItem(attr));
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.load(NumericFields.class, attr.get(KEY_NAME).getS());
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class ComplexType {

        public String key;
        public ComplexType type;

        public ComplexType(String key, ComplexType type) {
            super();
            this.key = key;
            this.type = type;
        }

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ComplexType getType() {
            return type;
        }

        public void setType(ComplexType type) {
            this.type = type;
        }
    }

    // Complex types are not supported by the V1 conversion schema
    @Test(expected = DynamoDBMappingException.class)
    public void testComplexTypeFailure() {
        DynamoDBMapperConfig config = new DynamoDBMapperConfig(ConversionSchemas.V1);
        DynamoDBMapper util = new DynamoDBMapper(dynamo, config);

        ComplexType complexType = new ComplexType("" + startKey++, new ComplexType("" + startKey++,
                null));
        util.save(complexType);
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class ComplexHashKeyType {

        private ComplexType key;
        private String attribute;

        @DynamoDBHashKey
        public ComplexType getKey() {
            return key;
        }

        public void setKey(ComplexType key) {
            this.key = key;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }
    }

    @Test(expected = DynamoDBMappingException.class)
    public void testUnsupportedHashKeyType() {
        ComplexType complexType = new ComplexType("" + startKey++, new ComplexType("" + startKey++,
                null));
        ComplexHashKeyType obj = new ComplexHashKeyType();
        obj.setKey(complexType);
        obj.setAttribute("abc");
        DynamoDBMapper util = new DynamoDBMapper(dynamo);
        util.save(obj);
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class NonSetCollectionType {

        private String key;
        private List<String> badlyMapped;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getBadlyMapped() {
            return badlyMapped;
        }

        public void setBadlyMapped(List<String> badlyMapped) {
            this.badlyMapped = badlyMapped;
        }
    }

    // Lists are not supported by the V1 conversion schema.
    @Test(expected = DynamoDBMappingException.class)
    public void testNonSetCollection() {
        DynamoDBMapperConfig config = new DynamoDBMapperConfig(ConversionSchemas.V1);
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo, config);

        NonSetCollectionType obj = new NonSetCollectionType();
        obj.setKey("" + startKey++);
        obj.setBadlyMapped(new ArrayList<String>());
        obj.getBadlyMapped().add("abc");
        mapper.save(obj);
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class FractionalVersionAttribute {

        private String key;
        private Double version;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @DynamoDBVersionAttribute
        public Double getVersion() {
            return version;
        }

        public void setVersion(Double version) {
            this.version = version;
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testFractionalVersionAttribute() {
        FractionalVersionAttribute obj = new FractionalVersionAttribute();
        obj.setKey("" + startKey++);
        obj.setVersion(0d);
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
        mapper.save(obj);
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class AutoGeneratedIntegerKey {

        private Integer key;
        private String value;

        @DynamoDBHashKey
        @DynamoDBAutoGeneratedKey
        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testAutoGeneratedIntegerHashKey() {
        AutoGeneratedIntegerKey obj = new AutoGeneratedIntegerKey();
        obj.setValue("fdgfdsgf");
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
        mapper.save(obj);
    }

    @DynamoDBTable(tableName = "aws-java-sdk-util")
    public static class AutoGeneratedIntegerRangeKey {

        private String key;
        private Integer rangekey;
        private String value;

        @DynamoDBHashKey
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @DynamoDBAutoGeneratedKey
        @DynamoDBRangeKey
        public Integer getRangekey() {
            return rangekey;
        }

        public void setRangekey(Integer rangekey) {
            this.rangekey = rangekey;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    @Test(expected = DynamoDBMappingException.class)
    public void testAutoGeneratedIntegerRangeKey() {
        AutoGeneratedIntegerRangeKey obj = new AutoGeneratedIntegerRangeKey();
        obj.setKey("Bldadsfa");
        obj.setValue("fdgfdsgf");
        DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
        mapper.save(obj);
    }

}