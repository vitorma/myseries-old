package mobi.myseries.testutil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.junit.rules.*;

/**
 * It is supposed to be used as a rule, but for some reason PowerMock doesn't want to work with it. So, I've made
 * a few edits to the original code so it may be used in setUp and tearDown methods.
 * 
 * For the original article and code, refer to
 * http://blog.cedarsoft.com/2011/12/junit-rule-fail-tests-on-exceptionsfailed-assertions-in-other-threads/
 * 
 * edit by Gabriel Assis Bezerra (gabriel@myseries.mobi).
 *
 * @author Johannes Schneider (js@cedarsoft.com)
 */
public class CatchAllExceptionsRule implements TestRule {
    private Thread.UncaughtExceptionHandler oldHandler;
    private List<Throwable> caught = new ArrayList<Throwable>();

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } catch ( Throwable t ) {
                    afterFailing();
                    throw t;
                }
                afterSuccess();
            }
        };
    }

    public void setUp() {
        this.before();
    }

    public void after() {
        this.afterSuccess();
    }

    private void before() {
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        caught = new ArrayList<Throwable>();

        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException( Thread t, Throwable e ) {
                caught.add( e );
                if ( oldHandler != null ) {
                    oldHandler.uncaughtException( t, e );
                }
            }
        } );
    }
 
    private void afterSuccess() {
        Thread.setDefaultUncaughtExceptionHandler( oldHandler );
 
        if ( !caught.isEmpty() ) {
            throw new AssertionError( buildMessage() );
        }
    }
 
    private String buildMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append( caught.size() ).append( " exceptions thrown but not caught in other threads:\n" );
 
        for ( Throwable throwable : caught ) {
            builder.append( "---------------------\n" );
 
            StringWriter out = new StringWriter();
            throwable.printStackTrace( new PrintWriter( out ) );
            builder.append( out.toString() );
        }
 
        builder.append( "---------------------\n" );
 
        return builder.toString();
    }
 
    private void afterFailing() {
        Thread.setDefaultUncaughtExceptionHandler( oldHandler );
    }
}