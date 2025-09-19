# Yap Task Manager

> “Productivity is never an accident. It is always the result of a commitment to excellence.” – Paul J. Meyer

Yap helps you **organise** and *track* your tasks with ease. It’s,

- text-based
- simple to use
- ~~boring~~ **fun** to try
- lightning fast ⚡

All you need to do is,

1. Download it from [here](https://github.com/kahei9299/ip).
2. Run it using `./gradlew run`.
3. Add your tasks.
4. Let Yap manage your to-do list for you 😉

---

## Features
- Managing tasks
- Managing deadlines
- Managing events
- Searching tasks with `find`

---

### Coming Soon
- [ ] Reminders
- [ ] Notifications
- [x] Add tasks
- [x] Delete tasks

---

### Example Usage

```java
public class Main {
    public static void main(String[] args) {
        new Yap("data/tasks.txt").run();
    }
}


## Product Screenshot

Here’s the full GUI of the application:

![Ui Screenshot](Ui.png)
