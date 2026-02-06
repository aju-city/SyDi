# Deliverable 1 – Work Plan

---

## WEEK 1

### Focus
System Interfaces & Component Diagram

### Objectives
- Finalise how all subsystems communicate
- Lock interface definitions across teams

---

### 1. IPOS Architecture Design

#### Tasks
- Identify all IPOS subsystems (SA, CA, PU)
- Define responsibilities, actors, and stakeholders of each subsystem
- Identify required interfaces between subsystems

#### Output
- Written architecture description
- Initial component breakdown

---

### 2. UML Component Diagram (System-Level)

#### Tasks
- Produce a UML **Component Diagram** showing:
  - All subsystems as components
  - All interfaces between subsystems
- Use **class-style interface notation**
- Do **not** include implementation details at this stage

#### Output
- Draft UML Component Diagram

---

### 3. Interface Specification (Cross-Team Agreement)

#### Tasks
For **each interface**:
- Agree on:
  - Data exchanged
  - Interface responsibility
- Specify:
  - Method names
  - Return types
  - Parameter names and types

#### Output
- Interface specification document or table

#### Example Interface Specification


| Field | Value |
|------|-------|
| **Interface** | name |
| **Provided by** | subsystem/actor |
| **Used by** | subsystem/actor |

#### Methods

| Method name | Return type | Parameters | Description |
|------------|------------|------------|-------------|
| - | - | - | - |







---

### Output by end of Week 1 (11/02)
- Agreed list of interfaces
- UML Component Diagram
- Interface method specifications

---

## WEEK 2

### Focus
Design Class Diagram (IPOS-PU)

### Objectives
- Translate interfaces into class-level design
- Separate **provided** vs **required** interfaces

---

### 4. Design Class Diagram (Subsystem-Level)

#### Tasks
- Produce a **Design Class Diagram fragment** for our own subsystem
- The diagram must show:
  - Implementation classes
  - Provided interfaces (implemented by own classes)
  - Required interfaces (used from other subsystems)
  - All method signatures
- Must match the interface definitions from Week 1 **exactly**
- No extra methods allowed

#### Output
- UML Design Class Diagram

---

### 5. Interface Traceability Check (Review Task)

#### Tasks
- Verify:
  - Every interface in the component diagram appears in the class diagram
  - All methods are consistent
  - No mismatched signatures

#### Output
- Updated diagrams (if required)

---

### 6. Java Interface Code

#### Tasks
- Create Java interfaces for all specified interfaces
- Include:
  - Correct method signatures
  - JavaDoc for:
    - Interface purpose
    - Each method
    - Parameters and return values

#### Output
- Java interface files

---

### 7. Implementation Classes (Own Subsystem)

#### Tasks
- Create implementation classes for:
  - Provided interfaces
- Include:
  - Methods or basic logic
- For required interfaces:
  - Implement **light-touch mock implementations**
  - Simulate external subsystem behaviour using:
    - In-memory data
    - Files

#### Output
- Java implementation classes
- Light-touch mock classes

---

### Output by end of Week 2 (18/02)
- Final Design Class Diagram (fragment)
- Complete Java interface code
- Implementation classes
- Mock implementations for required interfaces

---

## WEEK 3

### Objectives
- Generate code directly from UML
- Enable independent subsystem development

---

### 8. Test Plan Development (TDD-Style)

#### Tasks
- For **every method** in implementation classes:
  - Define test cases
- Use table format exactly as specified:
  - Column 1: Method name
  - Column 2: Test ID
  - Column 3: Parameter values
  - Column 4: Setup / Preconditions
  - Column 5: Expected outcome

#### Include
- Plausible values
- Implausible values
- Expected exceptions

#### Output
- Complete test plan tables

---

### Output by end of Week 3 (25/02)
- Complete test plan tables

---

## WEEK 4 (Two Days to Submission)

### Focus
Final Review

### Objectives
- Ensure full alignment with marking criteria

---

### 9. Final Validation & Consistency Review

**Participants:** Whole team

#### Tasks
- Check:
  - UML ↔ Java code consistency
  - Java ↔ test plan consistency
  - Interface compliance with agreed definitions
- Ensure formatting and terminology are consistent

---

### Output by Submission Day (27/02, 13:00)
- All required UML diagrams
- Java interfaces and implementation classes
- Test plan tables
