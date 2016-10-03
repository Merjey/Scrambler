package test;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;

/**
 * @see ScramblerTest#main
 */
public class TestListener extends RunListener{
	@Override
    public void testStarted(Description desc) {
        System.out.println("Started:" + desc.getDisplayName());
    }
 
    @Override
    public void testFinished(Description desc) {
        System.out.println("Finished:" + desc.getDisplayName());
    }
 
    @Override
    public void testFailure(Failure fail) {
        System.err.println("Failed:" + fail.getDescription().getDisplayName() + " [" + fail.getMessage() + "]");
    }
}
