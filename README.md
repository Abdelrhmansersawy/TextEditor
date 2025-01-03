# TextEditor

A feature-rich GUI plaintext editor written in Java Swing.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Core Features](#core-features)
- [Technical Implementation](#technical-implementation)
- [How to Contribute](#how-to-contribute)

## Features

- Create, open, and save plain text documents
- Multi-tab support
- Undo/redo functionality
- Find and replace capabilities
- Auto-save functionality

## Installation & Usage

Ensure you have Java Development Kit (JDK) installed

**Create output directory and compile**

```bash
mkdir -p out && javac -cp "library/*:src" src/**/*.java -d out
```

**Run the application**

```bash
java -cp "out:library/*" Main
```

# 

## Core Features

### File Operations

<details>
<summary>Create, Open, and Save Documents</summary>

- Open files using the **"Open"** menu item and select through the file chooser dialog

- Save files using the **"Save"** menu item with automatic overwrite protection
  ![ScreenShot](ScreenShots/openfile.gif)
  ![ScreenShot](ScreenShots/savefile.gif)
  
  </details>

<details>
<summary>Multi-Tab Support</summary>

Work with multiple documents simultaneously
![ScreenShot](ScreenShots/multitab.gif)

</details>

### Advanced Features

#### Spell Checker

Creating a smart spell-checking system that suggests corrections in real-time as you type. Here’s how it works:
 Dictionary Management:

- The system starts by loading a list of words from a dictionary file. If the file isn’t available, it uses a basic set of common words.

- As you type new words, the system "learns" them and adds them to the dictionary, making it smarter over time.
  Suggestions:

- If you misspell a word, the system suggests up to 7 possible corrections based on how close the word is to others in the dictionary.

> You can load any dictionary file, for me I used this one [1000 English word](https://github.com/first20hours/google-10000-english/blob/master/google-10000-english.txt)

Here's the guide to activiate it

1. Download [google-10000-english.txt](https://github.com/first20hours/google-10000-english/blob/master/google-10000-english.txt)

2. Move it to your project directory and put it into /library/

<details>
<summary>Find and Replace</summary>

- Implements KMP string pattern search algorithm (O(n + m) complexity)
- Supports both single and bulk replacements

**Performance Comparison:**
| Operation | Brute force | KMP |
| ---- | ---- | ---- |
| `search` | O(n * m) | O(n+m) |

![ScreenShot](ScreenShots/find.gif)
![ScreenShot](ScreenShots/replace.gif)

</details>

<details>
<summary>Auto-Save</summary>

Automatically tracks and saves changes
![ScreenShot](ScreenShots/autoSave.gif)

</details>

## Technical Implementation

### Undo/Redo System

- Implemented using Command Pattern design pattern
- Utilizes Stack and Rope data structures
  ![Architecture](ScreenShots/command_pattern.png)
  ![ScreenShot](ScreenShots/undo.gif)

### Rope Data Structure

A specialized data structure for efficient string manipulation with O(log n) operations.

> [!NOTE]
> The Rope data structure implementation is based on Treap data structure for balanced operations.

#### Implementation Details

- Based on Treap (Balanced Binary Search Tree)
- Uses randomization and Binary Heap properties

**Performance Comparison:**
| Operation | Vector/String | Rope |
| ---- | ---- | ---- |
| Build | O(n) | O(n log n) |
| Insert | O(n m) | O(m log n) |
| Erase | O(n) | O(log n) |
| Concat | O(m) | O(log n) |

## How to Contribute

1. Fork the repository
2. Create a new branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Run tests if available
5. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
6. Push to the branch (`git push origin feature/AmazingFeature`)
7. Create a Pull Request
