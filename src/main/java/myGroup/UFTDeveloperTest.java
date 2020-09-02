/*
 * note: This is a very complete test
 * It shows almost oll of what UFT Dev can do
 * Probably more than you can show a customer in a demo
 * But meant to be a resource for Pre-Sales
 */
package myGroup;

import myGroup.myAppModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.hp.lft.sdk.*;
import com.hp.lft.sdk.web.*;
import com.hp.lft.verifications.*;

import unittesting.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UFTDeveloperTest extends UnitTestClassBase {

    public UFTDeveloperTest() {
        //Change this constructor to private if you supply your own public constructor
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        instance = new UFTDeveloperTest();
        globalSetup(UFTDeveloperTest.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        globalTearDown();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws GeneralLeanFtException {
        Browser browser = BrowserFactory.launch(BrowserType.CHROME);
        browser.sync();

        browser.navigate("http://advantageonlineshopping.com/");
        browser.sync();

        Link userMenuLink = browser.describe(Link.class, new LinkDescription.Builder()
                .innerText("My account My orders Sign out ")
                .tagName("A").build());
        userMenuLink.click();

        EditField usernameEditField = browser.describe(EditField.class, new EditFieldDescription.Builder()
                .name("username")
                .tagName("INPUT")
                .type("text").build());
        usernameEditField.setValue("<your user name>");

        /*
         * I changed the recorded setSecure method to setValue, to make it easier to use your password
         * If you cnage back to setSecure,you generate the encrypted value from either
         * PasswordEncoder.exe
         */

        EditField passwordEditField = browser.describe(EditField.class, new EditFieldDescription.Builder()
                .name("password")
                .tagName("INPUT")
                .type("password").build());
        passwordEditField.setValue("<your password>");

        Button signInBtnundefinedButton = browser.describe(Button.class, new ButtonDescription.Builder()
                .buttonType("button")
                .name("SIGN IN")
                .tagName("BUTTON").build());
        signInBtnundefinedButton.click();

        Link speakersCategoryTxtLink = browser.describe(Link.class, new LinkDescription.Builder()
                .innerText("SPEAKERS")
                .tagName("SPAN").build());
        speakersCategoryTxtLink.click();

        WebElement boseSoundlinkBluetoothSpeakerIIIWebElement = browser.describe(WebElement.class, new WebElementDescription.Builder()
                .innerText("Bose Soundlink Bluetooth Speaker III")
                .tagName("A").build());
        boseSoundlinkBluetoothSpeakerIIIWebElement.click();

        EditField quantityEditField = browser.describe(EditField.class, new EditFieldDescription.Builder()
                .name("quantity")
                .tagName("INPUT")
                .type("text").build());
        quantityEditField.setValue("2");

        Button saveToCartButton = browser.describe(Button.class, new ButtonDescription.Builder()
                .buttonType("submit")
                .name("ADD TO CART")
                .tagName("BUTTON").build());
        saveToCartButton.click();

        /*
         as recorded

         Link shoppingCartLink = browser.describe(Link.class, new LinkDescription.Builder()
             .accessibilityName("ShoppingCart")
             .innerText("2 ")
             .role("link")
            .tagName("A").build());

         Below is as a modified dynamic value generated by using the OIC to make the
         innerText a regular expression so that it will work even if number of items changes

         Also, note the ' ' character after the number. I have found that the existence of this space
         is browser dependent

         That is why I added .* at the end
         */

        Link shoppingCartLink = browser.describe(Link.class, new LinkDescription.Builder()
                .accessibilityName("ShoppingCart")
                .innerText(new RegExpProperty("\\d.*"))
                .role("link")
                .tagName("A").build());

        shoppingCartLink.click();
        /*
         * this is a recorded checkpoint. But - there is really no flexibility here
         * also - beware the hard coded amount in the Name
         * so going to comment out and replace with resilient code
         Button checkOutBtnButton = browser.describe(Button.class, new ButtonDescription.Builder()
             .buttonType("submit")
             .name("CHECKOUT ($539.98)")
             .tagName("BUTTON").build());
         Verify.areEqual("CHECKOUT ($539.98)", checkOutBtnButton.getText(), "Verification", "Verify property: text");
         */

        /*
         * so lets use an Application Model to get the checkout button regardless of the value
         */
        myAppModel appModel = new myAppModel(browser);

        /*
         * Examine CheckOutBtnButton in the Application Model to see the dynamic description
         */
        String checkoutButtonString = appModel.CheckOutBtnButton().getText();

        /*
         * going to use a regular expression to grab just the value
         * the regex discards everything up to the first number, and everything after the last number
         * It will capture and return all numbers, regardless of the number of ' , ' characters
         * Note that ( and $ are "special characters" in a regex, so must be escaped with a ' \ '
         * But in java ' \ ' is also special, so must be replaced with  " \\ "
         * And - hard to see, but this regex uses multiple groupings (\d+)|,, to grab sets of digits ore a comma
         * and at he end, it gets ' . ' followed by two digits
         *
         * I need to declare the dblAmount outside of the if/then/else because of Java scoping rules
         * Otherwise, it "disappears" at the end of the block
         */

        double  dblCartAmount = 0.0;

        Pattern p = Pattern.compile("CHECKOUT \\(\\$(((\\d+)|,)+\\.\\d{2})\\)");   // the pattern to search for
        Matcher m = p.matcher(checkoutButtonString);

// now try to find at least one match
        if (m.find()) {
            System.out.println("Found a match");
            // we're only looking for one group, so get it
            String cartAmount = m.group(1);
            /*
             * get to get rid of (possible) ' , ' characters
             */
            cartAmount = cartAmount.replace(",", "");
//            System.out.println(cartAmount);
            dblCartAmount = Double.parseDouble(cartAmount); // convert string to double
            System.out.format("'%s'\n", dblCartAmount); // you don't need the format, but I thouhgt I would show it.

        } else {
            System.out.println("Did not find a match");
        }

        Verify.greaterOrEqual( 539.98, dblCartAmount, "Custom Verify", "Verify Amount is big enough");

        /*
         * now complete the purchase
         */

        appModel.CheckOutBtnButton().click();

        /*
         * If the NextBtnButton object is not found, it is probably because you did
         * modify the script (above) to login with valid credentials
         */
        appModel.NextBtnButton().click();
        appModel.SafepayUsernameEditField().setValue("safepayusername"); // 15 characters max

        /*
         * Note - you generate the encrypted value above from
         * PasswordEncoder.exe
         * If you change, be sure the PW meets complexity requirements
         */
        appModel.SafepayPasswordEditField().setSecure("5f4e99a9c758f525845cda8b");
        appModel.PayNowBtnSAFEPAYButton().click();

        // and back to a recording, including a verification
        WebElement thankYouForBuyingWithAdvantageWebElement = browser.describe(WebElement.class, new WebElementDescription.Builder()
                .innerText("Thank you for buying with Advantage")
                .tagName("SPAN").build());
        Verify.areEqual("Thank you for buying with Advantage", thankYouForBuyingWithAdvantageWebElement.getInnerText(), "Purchase Completed", "Verify property: innerText");

        Link hOMELink = browser.describe(Link.class, new LinkDescription.Builder()
                .innerText("HOME")
                .tagName("A").build());
        hOMELink.click();


    }

}