package br.edu.ufcg.aweseries;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.TextView;
import br.edu.ufcg.aweseries.gui.MySeries;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

    private MySeries myActivity;

    @Before
    public void setUp() {
        this.myActivity = new MySeries();
        this.myActivity.onCreate(null);
    }

    @Test
    public void helloWorld() {
        String hello = new MySeries().getResources()
                .getString(R.string.hello);

        assertThat(hello, equalTo("Hello World, MainActivity!"));
    }
    
    @Test
    public void mySeriesButtonText() {
        TextView textMySeries = (TextView) this.myActivity
                .findViewById(R.id.textView1);

        assertThat(textMySeries.getText().toString(), equalTo("My Series"));
    }
}
