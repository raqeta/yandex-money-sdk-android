# Yandex.Money SDK for Android

## Overview

This library provides tools for performing payments on devices running Android 4.0 (API 14) or higher.

Features:

* default activity for payment process;
* handling of payment process in an `IntentService`;
* handling of application's *instance id*;
* storing of saved cards.

The library uses [Yandex.Money SDK for Java][1].

## Usage

The simpliest way of using this library is to show `PaymentActivity` passing corresponding payment parameters. Payment process will be handled by the library. When done calling activity will receive `RESULT_OK` or `RESULT_CANCELED` depending on operation success.

For example, if you want to pay for a phone, you can do it like this:

```Java
public class MyActivity extends Activity implement View.OnClickListener {

	private static final String CLIENT_ID = "[your_client_id]";
	private static final int REQUEST_CODE = 1;

	...

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_CODE && resultCode = RESULT_OK) {
    		// payment was successful
    	}
    }

    ...

	@Override
	public void onClickListener(View v) {
		PaymentActivity.startForResult(MyActivity.this, CLIENT_ID,
			new ParamsPhone("79012345678", new BigDecimal(100.0)), REQUEST_CODE);
	}
}
```

It is also possible to use this library partially, for example if you want your own implementation of UI elements.

## Sample Project

The library contains sample project in module `sample`.

To run this sample project you should enter valid `clientId` in `PayActivity`. To obtain *client id* please visit [this page][2] (also available in [Russian][3]).

## Links

1. Yandex.Money API (in [English][4], in [Russian][5])
2. [Yandex.Money SDK for Java][1]

[1]: https://github.com/yandex-money/yandex-money-sdk-java
[2]: http://api.yandex.com/money/doc/dg/tasks/register-client.xml
[3]: http://api.yandex.ru/money/doc/dg/tasks/register-client.xml
[4]: http://api.yandex.com/money/
[5]: http://api.yandex.ru/money/