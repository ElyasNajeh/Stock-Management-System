# Smart Warehouse

A JavaFX desktop application for managing warehouse categories, products, stock quantities, and incoming shipments.

## Features

- Load categories, products, and shipments from CSV or text files.
- Add, view, search, update, reassign, and remove categories.
- Add, search, sort, update, and remove products.
- Add and process pending shipments in queue order.
- Approve or cancel shipments, with undo and redo support.
- Track product quantities and export the shipment activity log.

## Technologies & Tools

- **Java 21** — application language and build target.
- **JavaFX 21** — desktop UI, tables, forms, dialogs, and file choosers.
- **Maven 3.9.11 Wrapper** — reproducible dependency management, build, and launch commands.
- **CSS** — shared JavaFX styling while retaining the original visual design.

## Data Structures

- **Doubly linked list** — stores categories and supports indexed navigation and removal.
- **Cursor-based array list** — stores products and canceled shipments using the existing cursor implementation.
- **Queue** — keeps pending shipments in processing order.
- **Stacks** — retain shipment actions for undo and redo.
- **JavaFX observable lists** — keep table views synchronized with the application data.

## Prerequisites

- JDK 21 or newer, with `JAVA_HOME` set to the JDK installation.
- Git.
- Internet access on the first build so the Maven Wrapper can download Maven and JavaFX.

## Getting Started

```bash
git clone https://github.com/ElyasNajeh/Stock-Management-System.git
cd Stock-Management-System
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd javafx:run
```

macOS or Linux:

```bash
./mvnw clean verify
./mvnw javafx:run
```

Load the sample files in this order: categories, products, then shipments.

## Project Structure

- `src/main/java/application/` — existing Java classes and package organization.
- `src/main/resources/application/` — images, icons, and JavaFX CSS.
- `src/main/resources/data/` — bundled sample CSV data.
- `pom.xml` and `.mvn/` — Maven dependencies, plugins, and Wrapper configuration.
- `~/.smart-warehouse/data/` — writable copies of sample data created on first launch.
- `~/.smart-warehouse/logs/actions.log` — runtime shipment activity log.

