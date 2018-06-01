package Chat_Client;

import static org.junit.Assert.*;
import org.junit.Test;

public class test {

	
	@Test
	public void test1() {
		System.out.println("Hello from my server");
		String actual = "amit tell as we can only do hello world test :)";
		assertEquals(actual, "amit tell as we can only do hello world test :)");
	}
}
