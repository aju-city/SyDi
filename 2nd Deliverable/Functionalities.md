## Backend Functionality Needed

## Backend Functionality Needed

---

## 1. Commercial Membership Application

When a commercial applicant fills in the form on the frontend:

Create a new row in the `ipos_sa.commercial_applications` table with:

- `company_name`
- `reg_number`
- `director_details`
- `business_type`
- `address`
- `email`
- `phone`
- `status` (default = `"submitted"`)
- `reviewed_by` (default = `null`)
- `notes` (default = `null`)

The database will automatically generate:

- `application_id`
- `submitted_at`

---

## 2. Director Details Handling

On the frontend, the company can choose how many directors to add.

For example, if they choose 3 directors, the form will collect:

- `FirstName`
- `LastName`
- `PhoneNumber`

for each director.

Backend should:

1. Take all entered directors from the frontend
2. Combine them into one formatted string
3. Store that string in `director_details` in the `ipos_sa.commercial_applications` table

Example format:

- `Director 1: Sarah Ali, 07123456789`
- `Director 2: Omar Khan, 07987654321`

There is no separate directors table.

---

## 3. Non-Commercial Member Registration

When someone registers as a non-commercial member:

Backend should:

1. Generate a random 10-digit numeric password
2. Create a row in the `NonCommercialMember` table with:
   - `Email`
   - generated `Password`
   - `MustChangePassword` = `true`
   - `TotalOrders` = `0`
   - `CreatedAt` = automatic timestamp
3. Generate and store `MemberAccountNo` after insert, for example `PU0001`
4. Return the generated password so it can be shown on the frontend

---

## 4. First Login / Change Password

When a non-commercial member logs in for the first time:

1. Backend checks `MustChangePassword`
2. If it is `true`, frontend should be told to redirect user to change password
3. User enters a new password
4. Backend updates the `Password`
5. Backend sets `MustChangePassword` to `false`

This should happen only on first login, or until the password is changed.

---

## 5. Login Functionality

Login should support both:

- Non-commercial members
- Admin users

---

### Flow:

1. User enters email/username + password

---

### Step 1: Check AdminUsers table

- If credentials match an entry in `AdminUsers`:
  - Login is successful
  - Frontend should redirect user to **admin view/dashboard**

---

### Step 2: Check Commercial Applications (SA)

- If email exists in `ipos_sa.commercial_applications`:
  - Reject login
  - Return message:

**Commercial applicants/members cannot log in here.**

---

### Step 3: Check NonCommercialMember table

- If email + password match:
  - If `MustChangePassword = true`:
    - Return response indicating **password change required**
    - Frontend should redirect to change password screen

  - Else:
    - Login successful
  

---

### Step 4: Invalid Login

If no match is found:

- Return:

**Invalid email/username or password**

---

## 6. Guest User Flow

Frontend will have a guest button on the front page.

Guest users should be able to:

- browse the catalogue
- browse promotion pages
- keep a temporary cart while they are on the site

Backend should support guest carts using:

- `ShoppingCart.customer_type = 'GUEST'`
- `ShoppingCart.guest_token`

At checkout:

- guest users must provide:
  - `email`
  - `delivery address`
  - `payment details`

Unlike members, guests are not registered and do not have `member_email`.

---

## 7. Catalogue (CA Integration)

Retrieve products from `ipos_ca.stock_items`.

Backend should support:

- get all products
- search products by name and description
- get product by ID
- check if sufficient stock is available

Important:

- only return the base price from CA
- do not apply promotions in the general catalogue

---

## 8. Shopping Cart

Backend should support:

- create cart for member
- create temporary cart for guest
- add item to cart
- update item quantity
- remove item
- retrieve cart items
- clear cart after checkout

Tables used:

- `ShoppingCart`
- `ShoppingCartItems`

---

## 9. Member 10th Order Discount

When a member is viewing their cart:

Backend should check whether their next completed order will be:

- 10th
- 20th
- 30th
- and so on

If yes:

- apply a 10% discount to the cart total

This is based on `NonCommercialMember.TotalOrders`.

The discount should only apply to members, not guests.

---

## 10. Final Stock Check at Checkout

When the user presses checkout:

Backend must do one final stock check against `ipos_ca.stock_items` before completing the order.

This is to make sure CA stock can still accommodate the order.

If stock is insufficient:

- stop the checkout
- return an error response for the frontend to show

---

## 11. Checkout / Order Creation

When checkout succeeds:

1. Create a row in `Orders` with:
   - `customer_type`
   - `member_email` (if member)
   - `customer_email`
   - `delivery_address`
   - `order_date`
   - `total_amount`
   - `status`

2. Insert items into `OrderItems`

3. If the user is a member and payment succeeds:
   - increment `TotalOrders`

---

## 12. Payment Processing

When user submits payment:

Create a row in `PaymentTransaction` with:

- `order_id`
- `amount`
- `payment_method`
- `masked_card_number`
- `payment_status`
- `processor_reference` if available
- `failure_reason` if failed

Backend should:

- handle success and failure
- return an appropriate response to the frontend

For now, payment success/failure can be simulated.

---

## 13. CA Stock Update After Successful Payment

If payment is successful:

Backend must reduce stock in `ipos_ca.stock_items` for each purchased item.

This should happen only after payment succeeds.

For each item in the order:

- find the matching `item_id` in `ipos_ca.stock_items`
- reduce `quantity` by the purchased amount

---

## 14. Order Tracking

Backend should support:

### Logged-in users:
- get all orders by `member_email`

Return:

- order details
- ordered items
- delivery address
- current status

---

## 15. Promotions (for admin users)

Backend should support:

- create promotion campaign
- add products to campaign with discount
- update campaign:
  - dates
  - discounts
- delete campaign
- terminate campaign early
- get all active campaigns
- get products for a campaign

Important:

- discount is stored per campaign item
- same product can exist in multiple campaigns
- discounted price should only appear inside the promotion page
- general catalogue must still show normal base price

---

## 16. Promotion Conflict Handling

Conflict rule:

If an item is already discounted in another campaign, the discount rate for that same item cannot be different in the new campaign.

Backend should:

- detect whether the product already exists in another campaign
- compare discount rates
- if the discount rate is different, reject the operation
- return a response the frontend can use to show a notification or popup

---

## 17. Promotion Pricing Logic

Backend should:

- catalogue page → return base price only
- promotion page → return:
  - base price
  - campaign discount rate
  - calculated discounted price

Different campaigns can show the same product at different discounted prices, but only inside their own promotion pages.

---

## 18. Email Logging

For now, no real email API so we ar e just storing them.

Backend should only generate and store email information in `EmailLog`.

Examples:

- when an order is completed, create an order confirmation email record
- when tracking information is needed, create a tracking email record
- when SA needs PU to send an email later, email information can be stored here

Store:

- `recipient_email`
- `email_type`
- `subject`
- `body`
- `sent_datetime`
- `send_status`
- `failure_reason`

For now this is logging only, not actual email sending.

---

## 19. SA Email Requests

Backend should be able to check whether SA has inserted an email request that PU needs to process using the `EmailLog` table.

---

## 20. Reports (for admin users)

Backend should support generating reports using:

- `ActivityLog`
- `Orders`
- `OrderItems`
- `PaymentTransaction`
- `PromotionCampaign`
- `PromotionCampaignItems`
- `ShoppingCart`
- `ShoppingCartItems`
- `NonCommercialMember`

The database should contain all the information needed as per the student brief, if not, let me know.


---

## 21. Activity Logging

Backend should log activity in `ActivityLog`.

Current table supports these event types:

- `CATALOGUE_VIEW`
- `CAMPAIGN_VIEW`
- `CAMPAIGN_CLICK`
- `PRODUCT_VIEW`
- `ADD_TO_CART`
- `CHECKOUT`
- `PURCHASE`

Backend should make sure these are recorded where needed.


---

## 22. Cross-Subsystem Interaction

Backend should support:

- reading stock from CA (`ipos_ca.stock_items`)
- reducing CA stock after successful online purchase
- inserting commercial applications into SA (`ipos_sa.commercial_applications`)
- checking email requests relevant to PU communication

---

## 23. Error Handling / Frontend Notifications

Backend should return clear responses that frontend can use for notifications, alerts, or popups.

Examples:

- invalid login
- must change password first
- duplicate registration
- commercial applicant cannot log in here
- insufficient stock
- payment failed
- promotion conflict
- invalid promotion data
- missing required checkout data

Write any backend code that might be needed for the frontend to integrate the popups, etc.
