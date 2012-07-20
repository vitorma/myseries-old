package mobi.myseries.gui.appwidget;

public class MyScheduleWidgetServiceSmall extends MyScheduleWidgetServiceLarge {

    public MyScheduleWidgetServiceSmall() {
        super("mobi.myseries.gui.appwidget.MyScheduleWidgetServiceSmall");
    }

    protected Class<MyScheduleWidgetProviderSmall> widgetClass() {
        return MyScheduleWidgetProviderSmall.class;
    }
}