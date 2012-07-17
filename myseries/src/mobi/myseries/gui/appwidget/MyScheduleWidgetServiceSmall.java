package mobi.myseries.gui.appwidget;

public class MyScheduleWidgetServiceSmall extends MyScheduleWidgetServiceLarge {
    public MyScheduleWidgetServiceSmall() {
        super("mobi.myseries.gui.appwidget.MyScheduleWidgetProviderSmall$MyScheduleWidgetServiceSmall");
    }

    protected Class widgetClass() {
        return MyScheduleWidgetProviderSmall.class;
    }
}