package ru.yandex.money.android.sample.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.By;
import com.jayway.android.robotium.solo.Solo;

import ru.yandex.money.android.sample.MainActivity;

/**
 * Created by dvmelnikov on 17/02/14.
 */
public class BasicTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public BasicTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNon3ds() throws Exception {
        performTest(Card.NON_3DS);
    }

    public void testW3ds() throws Exception {
        performTest(Card.W_3DS);
    }

    private void performTest(Card card) {
        solo.waitForText("Название платежа");
        solo.typeTextInWebElement(By.id(Card.ID_CARD_NUMBER), card.getNumber());
        solo.typeTextInWebElement(By.id(Card.ID_MONTH), card.getMonth());
        solo.typeTextInWebElement(By.id(Card.ID_YEAR), card.getYear());
        solo.typeTextInWebElement(By.id(Card.ID_CVC), card.getCvc());
        solo.typeTextInWebElement(By.id(Card.ID_FIO), card.getFio());
        solo.typeTextInWebElement(By.id(Card.ID_CPS_EMAIL), card.getEmail());
        solo.clickOnWebElement(By.id("mobile-cps_submit-button"));
        solo.sleep(100000);
    }
}
