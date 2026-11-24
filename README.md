# Vanigam Clothing 1.1.0 — Android

Install Vanigam-Clothing-v1.1.0.apk on Android 8.0 or newer. Activate using one of the two owner-provided activation keys, then create the
owner name, shop name and unique 4-digit PIN. No default PIN is supplied.

## First setup

1. More > Settings: shop name, address, phone, optional GSTIN, logo and bill footer.
2. Selling prices are tax-inclusive. Included tax rate defaults to 0; set a rate
   only if appropriate for your store. This version has one store-wide tax rate.
3. More > Products: each size/colour is a separate product code, with selling price,
   opening stock, cost per piece and low-stock threshold. Tamil/English supported.
4. More > Users: create staff PINs. Duplicate PINs are rejected, even for disabled
   users. Administrators manage products, purchases, expenses, users and settings.
   Staff can bill and view their own sales/collections; they cannot view P&L.
5. Each cashier records their own opening cash once per day.
6. Settings > Choose folder: create/select a local folder inside Documents, such as
   Documents/VanigamClothing. The first Excel backup is created immediately.

## Billing and points

Search product name/code/size/colour, tap to add, change pieces with +/−. Customer
mobile is optional. Cash, UPI and card can share payment; cash balance updates as
you type. Save & print commits bill, stock and points together before opening Android
printing. A paid request uses a unique key, so retrying it cannot duplicate the bill.
The unpaid cart is retained on this phone separately for each signed-in user.

Default loyalty: ₹100 of final paid purchase earns 1 whole point, worth ₹1.
At least 100 existing points are required before any redemption. Once eligible,
customers may redeem up to their current balance while leaving a positive bill.
Earning spend, point value, threshold and ON/OFF are configurable in Settings.
Points are earned on the final paid amount, including configured tax, after discounts
and redemption. Customer is identified by 10-digit mobile (+91 accepted).

## A5 print

The bill snapshots shop details/logo, cashier, customer, items, payment and points.
Save & print opens the Android print dialog with A5 selected. Choose Save as PDF or
an Android-compatible printer service. The product header repeats across pages.
Reprint via Reports > Sales & bills. This app does not implement direct Bluetooth
thermal-printer protocols or silently bypass the Android print dialog.

## Excel reports, backup and restore

Reports include sales/bill lines, cashier/day collections, opening cash, best sellers,
P&L, current stock, period stock movements and customer points; all export genuine
.xlsx files. Filter the date range and tap Excel to choose a destination.

Daily backup is a complete versioned workbook, including restore data. It runs in
periodic background jobs (about every six hours) and checks on opening/resuming the
app; it skips only when today's backup already matches the data version. Multiple
versions can be saved in a day. Existing backups are never overwritten or deleted.
A backup includes sensitive customer and account recovery data: keep the folder
private and keep an additional copy off the phone when practical.

Android can defer background work, especially when battery restricted, force-stopped
or powered off. It cannot promise an exact midnight backup. Allow background activity
and Auto-start on Xiaomi; Settings displays the last successful backup and failures.
Use Back up now when closing the day. If permission to the folder is lost, select it
again. Uninstalling removes the app's local data but not the user-selected backup.

Settings > Restore asks before replacing the local store. Choose an unchanged original
Vanigam backup workbook, not a report export. Sign in with the PIN from that backup.

P&L is an operating estimate: sales minus included tax, snapshot cost of sold products,
expenses and wastage. Purchase stock is not deducted again. Correct costs must be
entered; the app does not maintain full statutory accounts, returns or bill corrections.
One phone/store; no cloud, multi-phone sync, camera barcode scanning or payment gateway.

## Build and verification

Android Studio project: compile SDK 35, target 34, minimum 26, Java 8, AGP 8.7.3
(Gradle 8.9 / JDK 17+). Open the project folder in Android Studio, sync and build.
Release APK provided was built with SDK 35 tools / ECJ and signed with the unique
key in signing/. KEEP THE KEY AND PASSWORD PRIVATE: future updates must use this
same certificate to preserve installed app data. Never commit these files publicly.

Tests: 47 Java core checks, 28 DOM interaction checks; genuine XLSX read/restore-data
round trip checked with openpyxl. APK signature, dex/resources and ZIP alignment
verified. Native Android execution, physical printer output, SAF restore and background
scheduling on a real device have NOT been tested. Headless Chromium was blocked by
the execution environment; DOM tests are not visual device tests.

## Activation and direct updates

Activation is required before login, setup or any business operation. It persists
through updates; clearing app data or reinstalling requires activation again.
The two owner-provided keys are shared offline keys, not device-bound licences.

Settings > App updates checks public GitHub Releases at
https://github.com/Sivasakthi7903/Vanigam . An administrator can change this repository.
Only a newer stable release with one Vanigam-Clothing-*.apk asset is accepted.
Downloads are checked for matching app identity and signing certificate, increasing
version code, size and GitHub SHA-256 digest when supplied. Android asks the user
to approve installation. Grant this app installation permission when requested.
Private repositories are not supported. No GitHub credentials are stored in the app.

### Publishing future updates

The included GitHub Actions workflow builds and publishes tags like v1.2.0.
Set repository Actions secrets ANDROID_KEYSTORE_BASE64 (base64 signing .p12),
ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_PASSWORD and ANDROID_KEY_ALIAS.
Keep the original signing key; alias is vanigam-clothing. The signing directory
is excluded from Git but included in this private source backup for the owner.
Do not upload this private source ZIP as a public release asset.
Publish only the signed APK and Git-tracked source. Repository releases must be
public for the in-app updater. Initial repository publication still requires access.

Additional verification: 22 activation/update policy checks and 6 activation/update
DOM checks passed. Native installation and real-device update flow remain untested.
