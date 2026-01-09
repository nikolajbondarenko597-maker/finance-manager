package com.promoit.finance.cli;

import com.promoit.finance.exception.ValidationException;
import com.promoit.finance.model.User;
import com.promoit.finance.service.AuthService;
import com.promoit.finance.service.FinanceService;

import java.util.Scanner;

public class FinanceApp {
    private final AuthService authService = new AuthService();
    private final FinanceService financeService = new FinanceService();
    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public static void main(String[] args) {
        new FinanceApp().run();
    }

    public void run() {
        System.out.println("Добро пожаловать в Personal Finance Manager!");
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> handleRegistration();
            case "2" -> handleLogin();
            case "3" -> {
                System.out.println("Выход...");
                System.exit(0);
            }
            default -> System.out.println("Неверный выбор");
        }
    }

    private void handleRegistration() {
        try {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine().trim();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();
            authService.register(login, password);
            System.out.println("Регистрация успешна!");
        } catch (ValidationException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            System.out.print("Логин: ");
            String login = scanner.nextLine().trim();
            System.out.print("Пароль: ");
            String password = scanner.nextLine().trim();
            currentUser = authService.login(login, password);
            System.out.println("Вход выполнен успешно!");
        } catch (ValidationException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Основное меню ===");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Показать отчёт");
        System.out.println("5. Выйти из аккаунта");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> addIncome();
            case "2" -> addExpense();
            case "3" -> setBudget();
            case "4" -> showReport();
            case "5" -> logout();
            default -> System.out.println("Неверный выбор");
        }
    }

    private void addIncome() {
        try {
            System.out.print("Категория дохода: ");
            String category = scanner.nextLine().trim();
            System.out.print("Сумма: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            financeService.addIncome(currentUser.getWallet(), category, amount);
            System.out.println("Доход добавлен.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: сумма должна быть числом");
        } catch (ValidationException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void addExpense() {
        try {
            System.out.print("Категория расхода: ");
            String category = scanner.nextLine().trim();
            System.out.print("Сумма: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            financeService.addExpense(currentUser.getWallet(), category, amount);

            // Проверка бюджета
            var remaining = financeService.getRemainingBudgets(currentUser.getWallet());
            if (remaining.containsKey(category) && remaining.get(category) < 0) {
                System.out.println("⚠ Превышен бюджет по категории '" + category + "'!");
            }

            // Проверка перерасхода
            double income = financeService.getTotalIncome(currentUser.getWallet());
            double expenses = financeService.getTotalExpenses(currentUser.getWallet());
            if (expenses > income) {
                System.out.println("⚠ Расходы превысили доходы!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: сумма должна быть числом");
        } catch (ValidationException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void setBudget() {
        try {
            System.out.print("Категория: ");
            String category = scanner.nextLine().trim();
            System.out.print("Лимит бюджета: ");
            double limit = Double.parseDouble(scanner.nextLine().trim());
            financeService.setBudget(currentUser.getWallet(), category, limit);
            System.out.println("Бюджет установлен.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: лимит должен быть числом");
        } catch (ValidationException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showReport() {
        var wallet = currentUser.getWallet();
        double totalIncome = financeService.getTotalIncome(wallet);
        double totalExpenses = financeService.getTotalExpenses(wallet);
        var incomeByCategory = financeService.getIncomeByCategory(wallet);
        var expensesByCategory = financeService.getExpensesByCategory(wallet);
        var remainingBudgets = financeService.getRemainingBudgets(wallet);

        System.out.println("\n=== ФИНАНСОВЫЙ ОТЧЁТ ===");
        System.out.printf("Общий доход: %.2f\n", totalIncome);
        System.out.println("Доходы по категориям:");
        incomeByCategory.forEach((cat, sum) -> System.out.printf("  %s: %.2f\n", cat, sum));

        System.out.printf("Общие расходы: %.2f\n", totalExpenses);
        System.out.println("Бюджет по категориям:");
        for (String cat : remainingBudgets.keySet()) {
            double limit = wallet.getBudgets().get(cat).getLimit();
            double remaining = remainingBudgets.get(cat);
            System.out.printf("  %s: %.2f, Оставшийся бюджет: %.2f\n", cat, limit, remaining);
        }
    }

    private void logout() {
        authService.logoutAndSave(currentUser);
        currentUser = null;
        System.out.println("Вы вышли из аккаунта. Данные сохранены.");
    }
}
