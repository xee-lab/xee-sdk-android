/*
 * Copyright 2016 Eliocity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import static org.hamcrest.CoreMatchers.is;

public class TestXeeEnv {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void cloud() {
        collector.checkThat("cloud host is incorrect", XeeEnv.CLOUD, is("cloud"));
    }

    @Test
    public void sandbox() {
        collector.checkThat("sandbox host is incorrect", XeeEnv.SANDBOX, is("sandbox"));
    }
}