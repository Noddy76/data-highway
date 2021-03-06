/**
 * Copyright (C) 2016-2019 Expedia, Inc.
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
package com.hotels.road.trafficcontrol;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.road.trafficcontrol.KafkaBrokerMonitor.AdminClient;

@RunWith(MockitoJUnitRunner.class)
public class KafkaBrokerMonitorTest {

  private @Mock AdminClient client;

  private KafkaBrokerMonitor underTest;

  @Before
  public void before() throws Exception {
    underTest = new KafkaBrokerMonitor(client, "1", "2");
  }

  @Test
  public void testName() throws Exception {
    doReturn(Collections.singletonList("0")).when(client).getBrokerIds();
    doReturn(new Properties()).when(client).getConfig("0");

    underTest.checkAndUpdateBrokers();

    ArgumentCaptor<Properties> captor = ArgumentCaptor.forClass(Properties.class);
    verify(client).changeConfig(eq("0"), captor.capture());

    Properties config = captor.getValue();

    assertThat(config.size(), is(2));
    assertThat(config.getProperty("leader.replication.throttled.rate"), is("1"));
    assertThat(config.getProperty("follower.replication.throttled.rate"), is("2"));
  }
}
