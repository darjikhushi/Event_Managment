package com.example.eventmanagment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {

    private TextInputLayout cardNumberLayout, cardHolderNameLayout, expiryDateLayout, cvcLayout;
    private TextInputEditText cardNumberEdit, cardHolderNameEdit, expiryDateEdit, cvcEdit;
    private MaterialButton payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        cardHolderNameLayout = findViewById(R.id.cardHolderNameLayout);
        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        cvcLayout = findViewById(R.id.cvcLayout);

        cardNumberEdit = findViewById(R.id.cardNumberEdit);
        cardHolderNameEdit = findViewById(R.id.cardHolderNameEdit);
        expiryDateEdit = findViewById(R.id.expiryDateEdit);
        cvcEdit = findViewById(R.id.cvcEdit);

        payButton = findViewById(R.id.payButton);

        // Make expiryDateEdit non-editable manually
        expiryDateEdit.setFocusable(false);
        expiryDateEdit.setClickable(true);

        // Open Month-Year picker on click
        expiryDateEdit.setOnClickListener(v -> showMonthYearPicker());

        payButton.setOnClickListener(v -> {
            if (validateInput()) {
                Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();

                Intent confirmationIntent = new Intent(PaymentActivity.this, PaymentConfirmation.class);
                confirmationIntent.putExtra("cardNumber", cardNumberEdit.getText().toString().trim());
                startActivity(confirmationIntent);
            }
        });
    }

    private void showMonthYearPicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PaymentActivity.this,
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    // Month is 0-based, so add 1
                    selectedMonth += 1;
                    String formattedMonth = (selectedMonth < 10 ? "0" : "") + selectedMonth;
                    String formattedDate = formattedMonth + "/" + (selectedYear % 100); // MM/YY
                    expiryDateEdit.setText(formattedDate);
                },
                year, month, calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Hide the day spinner
        try {
            java.lang.reflect.Field[] datePickerFields = datePickerDialog.getDatePicker().getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : datePickerFields) {
                if (field.getName().equals("mDaySpinner")) {
                    field.setAccessible(true);
                    Object dayPicker = field.get(datePickerDialog.getDatePicker());
                    ((android.view.View) dayPicker).setVisibility(android.view.View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }

    private boolean validateInput() {
        cardNumberLayout.setError(null);
        cardHolderNameLayout.setError(null);
        expiryDateLayout.setError(null);
        cvcLayout.setError(null);

        String cardNumber = cardNumberEdit.getText().toString().trim();
        String cardHolderName = cardHolderNameEdit.getText().toString().trim();
        String expiryDate = expiryDateEdit.getText().toString().trim();
        String cvc = cvcEdit.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberLayout.setError("Card number is required");
            isValid = false;
        } else if (cardNumber.length() != 16) {
            cardNumberLayout.setError("Card number must be 16 digits");
            isValid = false;
        }

        if (TextUtils.isEmpty(cardHolderName)) {
            cardHolderNameLayout.setError("Cardholder name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(expiryDate)) {
            expiryDateLayout.setError("Expiry date is required");
            isValid = false;
        } else {
            Pattern pattern = Pattern.compile("(0[1-9]|1[0-2])/([0-9]{2})");
            Matcher matcher = pattern.matcher(expiryDate);
            if (!matcher.matches()) {
                expiryDateLayout.setError("Invalid format (MM/YY)");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(cvc)) {
            cvcLayout.setError("CVC is required");
            isValid = false;
        } else if (cvc.length() != 3) {
            cvcLayout.setError("CVC must be 3 digits");
            isValid = false;
        }

        return isValid;
    }
}
