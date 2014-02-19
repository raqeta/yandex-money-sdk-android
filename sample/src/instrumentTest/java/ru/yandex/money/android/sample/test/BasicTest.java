package ru.yandex.money.android.sample.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.By;
import com.jayway.android.robotium.solo.Solo;
import com.jayway.android.robotium.solo.WebElement;

import java.util.ArrayList;

import ru.yandex.money.android.sample.MainActivity;

/**
 * Created by dvmelnikov on 17/02/14.
 */
public class BasicTest extends ActivityInstrumentationTestCase2 {

    private static final String TAG = BasicTest.class.getName();

    private Solo solo;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public BasicTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testRequest() throws Exception {

        solo.waitForText("Название платежа");

        solo.typeTextInWebElement(By.id(Card.ID_CARD_NUMBER), Card.non3ds.getNumber());
        solo.typeTextInWebElement(By.id(Card.ID_MONTH), Card.non3ds.getMonth());
        solo.typeTextInWebElement(By.id(Card.ID_YEAR), Card.non3ds.getYear());
        solo.typeTextInWebElement(By.id(Card.ID_CVC), Card.non3ds.getCvc());
        solo.typeTextInWebElement(By.id(Card.ID_FIO), Card.non3ds.getFio());
        solo.typeTextInWebElement(By.id(Card.ID_CPS_EMAIL), Card.non3ds.getEmail());
        solo.clickOnWebElement(By.id("mobile-cps_submit-button"));
        solo.sleep(5000);
//        solo.clickOnWebElement(By.cssSelector(//*[@id="mobile-cps__content"]/form/table/tbody/tr[10]/td/div/input));
    }
}
