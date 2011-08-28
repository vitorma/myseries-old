package br.edu.ufcg.aweseries;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        this.mainActivity = new MainActivity();
        this.mainActivity.onCreate(null);
    }

    @Test
    public void helloWorld() {
        String hello = new MainActivity().getResources()
                .getString(R.string.hello);

        assertThat(hello, equalTo("Hello World, MainActivity!"));
    }
    
    @Test
    public void mySeriesButtonText() {
        Button btMySeries = (Button) this.mainActivity
                .findViewById(R.id.button1);

        assertThat(btMySeries.getText().toString(), equalTo("My Series"));
    }
}
