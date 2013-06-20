package mobi.myseries.test.unit.application.notification;

import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.NotificationLauncher;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class NotificationLauncherTest {

    // Construction

    @Test(expected=IllegalArgumentException.class)
    public void itDoesNotAllowNullDefaultNotificationDispatcher() {
        new NotificationLauncher(null);
    }

    @Test
    public void theDefaultDispatcherIsTheFirstToBeUsed() {
        NotificationDispatcher dispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(dispatcher);
        Notification notification = mock(Notification.class);

        launcher.launch(notification);

        verify(dispatcher).notify(notification);
    }

    // Setting a new dispatcher

    @Test(expected=IllegalArgumentException.class)
    public void itDoesNotAllowSettingANullDispatcher() {
        NotificationDispatcher dispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(dispatcher);

        launcher.setDispatcherTo(null);
    }

    @Test
    public void whenANewDispatcherIsSetTheNotificationInTheLastDispatcherIsCanceled() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.launch(notification);
        launcher.setDispatcherTo(newDispatcher);

        verify(defaultDispatcher).cancel(notification);
    }

    @Test
    public void whenANewDispatcherIsSetTheNotificationIsRelauched() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.launch(notification);
        launcher.setDispatcherTo(newDispatcher);

        verify(newDispatcher).notify(notification);
    }

    @Test
    public void whenANewDispatcherIsSetAndThereAreNotRunningNotificationsThenNotificationsAreNotLauchedOrCanceled() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);

        launcher.setDispatcherTo(newDispatcher);

        verifyZeroInteractions(newDispatcher);
        verifyZeroInteractions(defaultDispatcher);
    }

    @Test
    public void whenANewDispatcherIsSetNewNotificationsGoToIt() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification firstNotification = mock(Notification.class);
        Notification secondNotification = mock(Notification.class);

        launcher.launch(firstNotification);

        launcher.setDispatcherTo(newDispatcher);
        launcher.launch(secondNotification);

        verify(defaultDispatcher).notify(firstNotification);
        verify(newDispatcher).notify(secondNotification);
    }

    // Removing a dispatcher

    @Test
    public void itIgnoresRemovingTheDefaultDispatcher() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.launch(notification);
        verify(defaultDispatcher).notify(notification);

        launcher.removeDispatcher(defaultDispatcher);
        verifyNoMoreInteractions(defaultDispatcher);
    }

    @Test
    public void itIgnoresRemovingADispatcherThatIsNotSet() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher notUsedDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.launch(notification);

        launcher.removeDispatcher(notUsedDispatcher);

        verifyZeroInteractions(notUsedDispatcher);
    }

    @Test
    public void afterADispatcherIsRemovedTheNotificationInItIsCanceled() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.setDispatcherTo(newDispatcher);
        launcher.launch(notification);

        launcher.removeDispatcher(newDispatcher);

        verify(newDispatcher).cancel(notification);
    }

    @Test
    public void afterADispatcherIsRemovedTheNotificationIsRelaunchedThroughTheDefaultDispatcher() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.setDispatcherTo(newDispatcher);
        launcher.launch(notification);

        verify(defaultDispatcher, never()).notify(notification);
        launcher.removeDispatcher(newDispatcher);

        verify(defaultDispatcher).notify(notification);
    }

    @Test
    public void afterADispatcherIsRemovedAndThereIsNoNotificationNothingIsLaunchedThroughTheDefaultDispatcher() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);

        launcher.setDispatcherTo(newDispatcher);
        launcher.removeDispatcher(newDispatcher);

        verifyZeroInteractions(defaultDispatcher);
    }

    @Test
    public void afterADispatcherIsRemovedTheDefaultDispatcherIsUsed() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        Notification notification = mock(Notification.class);

        launcher.setDispatcherTo(newDispatcher);
        launcher.removeDispatcher(newDispatcher);

        verifyZeroInteractions(defaultDispatcher);
        launcher.launch(notification);

        verify(defaultDispatcher).notify(notification);
    }

    // Launching notifications

    @Test(expected=IllegalArgumentException.class)
    public void itDoesNotAllowLaunchingNullNotifications() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);

        launcher.launch(null);
    }

    // Canceling notifications

    @Test
    public void itIgnoresCancelingANotificationThatIsNotTheCurrentOne() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);
        int notUsedNotificationId = 0;

        launcher.cancel(notUsedNotificationId);

        verifyZeroInteractions(defaultDispatcher);
    }

    @Test
    public void itCancelsANotificationThatIsCurrentOne() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);

        int notificationId = 0;
        Notification notification = mock(Notification.class);
        when(notification.id()).thenReturn(notificationId);

        launcher.launch(notification);
        launcher.cancel(notificationId);

        verify(defaultDispatcher).cancel(notification);
    }

    @Test
    public void changingDispatcherDoesNotRelaunchesACanceledNotification() {
        NotificationDispatcher defaultDispatcher = mock(NotificationDispatcher.class);
        NotificationDispatcher newDispatcher = mock(NotificationDispatcher.class);
        NotificationLauncher launcher = new NotificationLauncher(defaultDispatcher);

        int notificationId = 0;
        Notification notification = mock(Notification.class);
        when(notification.id()).thenReturn(notificationId);

        launcher.launch(notification);
        launcher.cancel(notificationId);

        launcher.setDispatcherTo(newDispatcher);

        verifyZeroInteractions(newDispatcher);
    }
}
