/*
 * Copyright (C) 2018 The Android Open Source Project
 *
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
 */

package com.android.car.settings.users;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.car.userlib.CarUserManagerHelper;
import android.content.pm.UserInfo;

import com.android.car.settings.CarSettingsRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

@RunWith(CarSettingsRobolectricTestRunner.class)
public class AddNewUserTaskTest {
    @Mock
    private CarUserManagerHelper mCarUserManagerHelper;
    @Mock
    private AddNewUserTask.AddNewUserListener mAddNewUserListener;

    private AddNewUserTask mTask;

    @Before
    public void createAsyncTask() {
        MockitoAnnotations.initMocks(this);
        mTask = new AddNewUserTask(mCarUserManagerHelper, mAddNewUserListener);
    }

    @Test
    public void testTaskCallsCreateNewNonAdminUser() {
        String newUserName = "Test name";
        mTask.execute(newUserName);
        Robolectric.flushBackgroundThreadScheduler();

        verify(mCarUserManagerHelper).createNewNonAdminUser(newUserName);
    }

    @Test
    public void testSwitchToNewUserIfUserCreated() {
        String newUserName = "Test name";
        UserInfo newUser = new UserInfo(10, newUserName, /* flags= */ 0);
        when(mCarUserManagerHelper.createNewNonAdminUser(newUserName)).thenReturn(newUser);

        mTask.execute(newUserName);
        Robolectric.flushBackgroundThreadScheduler();

        verify(mCarUserManagerHelper).switchToUser(newUser);
    }

    @Test
    public void testOnUserAddedSuccessCalledIfUserCreated() {
        String newUserName = "Test name";
        UserInfo newUser = new UserInfo(10, newUserName, /* flags= */ 0);
        when(mCarUserManagerHelper.createNewNonAdminUser(newUserName)).thenReturn(newUser);

        mTask.execute(newUserName);
        Robolectric.flushBackgroundThreadScheduler();

        verify(mAddNewUserListener).onUserAddedSuccess();
    }

    @Test
    public void testOnUserAddedFailureCalledIfNullReturned() {
        String newUserName = "Test name";
        when(mCarUserManagerHelper.createNewNonAdminUser(newUserName)).thenReturn(null);

        mTask.execute(newUserName);
        Robolectric.flushBackgroundThreadScheduler();

        verify(mAddNewUserListener).onUserAddedFailure();
    }
}
