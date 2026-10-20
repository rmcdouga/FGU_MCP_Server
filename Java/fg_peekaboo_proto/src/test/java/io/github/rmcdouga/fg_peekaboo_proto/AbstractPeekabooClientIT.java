package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;

import io.github.rmcdouga.fg_peekaboo_proto.PeekabooClient.ImageInfo;


@Disabled("This test is only for manual integration testing on a machine with both Fantasy Grounds and Peekaboo installed.")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractPeekabooClientIT {
	private static final String FG_BUNDLE_ID = "unity.SmiteWorks.Fantasy Grounds"; // The bundle identifier for Fantasy Grounds (macOS)

	private final PeekabooClient underTest;
	private long fgProcessId;
	
	public AbstractPeekabooClientIT(PeekabooClient underTest) {
		this.underTest = underTest;
	}

	@Test
	@Order(1)
	void testAppLaunch() {
		long result = underTest.appLaunch(FG_BUNDLE_ID);
		
		fgProcessId = result;
		assertThat(result).as("The Fantasy Grounds process ID should be greater than 0 after launch.")
						  .isGreaterThan(0);
	}

	@Disabled("The image command does not work, so this will always fail.")
	@Test
	@Order(2)
	void testImageAppTargetImageFormatImageScalePath(@TempDir Path tempDir) {
		validateFgProcessId();
		Path screenshotPath = tempDir.resolve("PeekabooMcpClientTest_testImageAppTargetImageFormatImageScalePath_screenshot.png");
		underTest.image(PeekabooClient.AppTarget.ProcessTarget.of(fgProcessId), PeekabooClient.ImageFormat.PNG, PeekabooClient.ImageScale.NATIVE, screenshotPath );
		
		assertThat(screenshotPath).as("Screenshot file should exist after image capture.")
								  .existsNoFollowLinks();
	}

	@Disabled("The image command does not work, so this will always fail.")
	@Test
	@Order(2)
	void testImageAppTargetImageScale() {
		validateFgProcessId();
		underTest.image(PeekabooClient.AppTarget.ProcessTarget.of(fgProcessId), PeekabooClient.ImageScale.NATIVE);
	}

	@Test
	@Order(2)
	void testSee(@TempDir Path tempDir) {
		validateFgProcessId();
		Path screenshotPath = tempDir.resolve("PeekabooMcpClientTest_testSee_screenshot.png");
		ImageInfo seeResult = underTest.see(PeekabooClient.AppTarget.ProcessTarget.of(fgProcessId), screenshotPath);
		
		assertThat(screenshotPath).as("Screenshot file should exist after see command.")
								  .existsNoFollowLinks();
		assertThat(seeResult.width()).as("Image width should be greater than 0.")
									.isGreaterThan(0);
		assertThat(seeResult.height()).as("Image height should be greater than 0.")
									.isGreaterThan(0);
	}

	@Test
	@Order(3)
	void testMove() {
		validateFgProcessId();
		underTest.appFocus(fgProcessId);
		underTest.move(PeekabooClient.AppTarget.ProcessTarget.of(fgProcessId), 600, 80);
//			underTest.click(fgProcessId, 600, 80);
	}

	@Test
	@Order(4)
	void testClick() {
		validateFgProcessId();
		underTest.click(fgProcessId, 600, 80);
	}

	private void validateFgProcessId() {
		if (fgProcessId == 0) {
			fgProcessId = FantasyGroundsApp.processId().orElse(0);
		}
		if (fgProcessId == 0) {
			fail("Fantasy Grounds process not found. Please ensure Fantasy Grounds is running before executing this test.");
		} else {
			System.out.printf("Fantasy Grounds process ID: %d%n", fgProcessId);
		}
	}
}
