## Backend Functionality Needed

## 1. Commercial Membership Application

When a commercial applicant fills in the form on the frontend:  

Create a new row in the CommercialApplicant table with:  

- `CompanyName`
- `CompanyRegistrationNumber`
- `BusinessType`
- `Email`
- `ApplicationDate`
- `ApplicationStatus` (default = `"Pending"`)
- `NotificationPreference`
- `CompanyAddress`

The database will automatically generate the ApplicationID.  

---

## 2. Adding Company Directors

On the frontend we added a director counter, so the company can choose how many directors to add.  

For example, if they choose 3 directors, the form will send:

- `FirstName`
- `LastName`
- `PhoneNumber`

for each director  

Backend should:

1. Take the ApplicationID from the newly created commercial application.
2. Insert each director into the CompanyDirector table.
3. Link them using the same `ApplicationID`.  

---

## 3. Non-Commercial Member Registration

For now we simplified this just to test login functionality.

When someone registers as a non-commercial member, create a row in the NonCommercialMember table with:

- `Email`
- `Password`
- `MustChangePassword` (default `false` for now)
- `TotalOrders` (default `0`)
- `CreatedAt` (automatic timestamp)

Later we will replace this with the random generated password system, but for now the frontend sends the password directly.

---

## 4. Login Functionality (Testing Only)

Login should only work for non-commercial members.

Flow:

1. User enters email + password  
2. Backend checks the NonCommercialMember table
3. If email + password match they login  

If the email exists in CommercialApplicant, return a message like:  
Commercial applicants/members can not log in here.  
