# 📦 Inventory Management System

A desktop Graphical User Interface (GUI) application built with **Java Swing** that allows businesses to manage their product inventory efficiently.

---

## 📸 Features

| Feature | Description |
|---|---|
| ➕ Add Products | Add a new product with Barcode, Name, Price, and Quantity |
| ✏️ Update Products | Edit existing product details instantly |
| 🗑️ Delete Products | Remove products from the inventory |
| 🔍 Barcode Search | Instantly find a product by typing a Barcode (O(1) search) |
| 🔴 Low Stock Alerts | Rows automatically highlighted in **red** when quantity < 5 |
| 💰 Total Value | Real-time calculation of total inventory value |
| 🔒 Validation | Prevents negative stock or price values |

---

## 🏗️ Project Structure

```
inventry/
├── src/
│   ├── Product.java          # Data model representing an inventory item
│   ├── InventoryManager.java # Singleton class managing all inventory data
│   └── InventoryApp.java     # Main Swing GUI application
│
├── out/                      # Compiled .class files (auto-generated)
│
├── run.bat                   # One-click compile & run script (Windows)
└── README.md                 # Project documentation (this file)
```

---

## 🧠 Core Engineering Concepts

### 1. Singleton Pattern — `InventoryManager`
Only **one instance** of `InventoryManager` ever exists, ensuring all parts of the app share the same data source.

```java
public static InventoryManager getInstance() {
    if (instance == null) {
        instance = new InventoryManager();
    }
    return instance;
}
```

### 2. Data Binding — `InventoryTableModel`
A custom `AbstractTableModel` is **bound directly** to the `List<Product>` inside the Manager. When data changes, `fireTableDataChanged()` is called, and the table auto-updates with no page reload needed.

### 3. Search/Filter Algorithm — O(1) Barcode Lookup
Products are stored in both a `List<Product>` (for the table) and a `Map<String, Product>` (for instant lookup). This means searching by barcode is **constant time**, regardless of inventory size.

```java
// O(1) lookup — HashMap in InventoryManager
Product p = manager.getProductByBarcode(query);
```

---

## 🛠️ How to Run

### Prerequisites
- Java JDK 17 or later installed
- Java added to system PATH

### Option 1: One-click (Windows)
Double-click **`run.bat`** in the project root.

### Option 2: Manual (Command Line)
```bash
# Step 1: Navigate to the project root
cd d:\inventry

# Step 2: Compile source files
javac -d out src/*.java

# Step 3: Run the application
java -cp out InventoryApp
```

---

## 📋 Usage Guide

1. **Add a Product**: Fill in the Barcode, Name, Price, and Quantity fields → Click **Save / Update**.
2. **Update a Product**: Click any row in the table to auto-fill the form → Modify values → Click **Save / Update**.
3. **Delete a Product**: Click a row, then click **Delete** and confirm.
4. **Search by Barcode**: Type a Barcode in the top "Fast Lookup" search box — it instantly selects the matching row.
5. **Clear Form**: Click **Clear Form** to reset all input fields.

---

## 🔬 Self-Audit Answers (Step 2: Critical Code Review)

| Question | Answer |
|---|---|
| Can I reduce stock below 0? | ❌ **No.** The app shows an error dialog and rejects the save. |
| Do fields clear after adding? | ✅ **Yes.** `clearForm()` is called after every successful save. |
| Does Total Value update instantly? | ✅ **Yes.** `refreshData()` recalculates and updates the label on every change. |

---

## ⚙️ Engineering Evolutions

### Tier 1 — Low Stock Alerts (Conditional Formatting)
A custom `DefaultTableCellRenderer` (`LowStockRenderer`) inspects each row at render-time. If `quantity < 5`, that row is highlighted in **light red** with red text.

### Tier 2 — Barcode Mockup (O(1) Search)
A `KeyListener` on the search field fires on each keystroke. The lookup is delegated to `InventoryManager.getProductByBarcode()`, which uses a `HashMap` for O(1) resolution — the matching row is auto-selected in the table.

---

## 👩‍💻 Author

**Course Task 2** — Inventory Management System with Basic GUI  
*Technologies: Java, Swing, Data Binding, Singleton Pattern, O(1) HashMap Search*
