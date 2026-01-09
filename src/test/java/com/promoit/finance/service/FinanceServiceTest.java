package com.promoit.finance.service;

import com.promoit.finance.model.Budget;
import com.promoit.finance.model.TransactionType;
import com.promoit.finance.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.promoit.finance.exception.ValidationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FinanceServiceTest {

    private FinanceService service;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        service = new FinanceService();
        wallet = new Wallet();
    }

    @Test
    void testAddIncome() {
        service.addIncome(wallet, "Зарплата", 50000.0);
        assertEquals(50000.0, service.getTotalIncome(wallet), 0.01);
    }

    @Test
    void testAddExpense() {
        service.addExpense(wallet, "Еда", 10000.0);
        assertEquals(10000.0, service.getTotalExpenses(wallet), 0.01);
    }

    @Test
    void testTotalIncomeWithMultipleTransactions() {
        service.addIncome(wallet, "Зарплата", 30000.0);
        service.addIncome(wallet, "Бонус", 10000.0);
        assertEquals(40000.0, service.getTotalIncome(wallet), 0.01);
    }

    @Test
    void testTotalExpensesWithMultipleTransactions() {
        service.addExpense(wallet, "Еда", 5000.0);
        service.addExpense(wallet, "Такси", 1500.0);
        assertEquals(6500.0, service.getTotalExpenses(wallet), 0.01);
    }

    @Test
    void testIncomeByCategory() {
        service.addIncome(wallet, "Зарплата", 30000.0);
        service.addIncome(wallet, "Зарплата", 20000.0);
        service.addIncome(wallet, "Премия", 10000.0);

        Map<String, Double> incomeByCat = service.getIncomeByCategory(wallet);
        assertEquals(50000.0, incomeByCat.get("Зарплата"), 0.01);
        assertEquals(10000.0, incomeByCat.get("Премия"), 0.01);
    }

    @Test
    void testExpensesByCategory() {
        service.addExpense(wallet, "Еда", 8000.0);
        service.addExpense(wallet, "Еда", 2000.0);
        service.addExpense(wallet, "Развлечения", 5000.0);

        Map<String, Double> expensesByCat = service.getExpensesByCategory(wallet);
        assertEquals(10000.0, expensesByCat.get("Еда"), 0.01);
        assertEquals(5000.0, expensesByCat.get("Развлечения"), 0.01);
    }

    @Test
    void testSetBudget() {
        service.setBudget(wallet, "Еда", 10000.0);
        Budget budget = wallet.getBudgets().get("Еда");
        assertNotNull(budget);
        assertEquals(10000.0, budget.getLimit(), 0.01);
    }

    @Test
    void testRemainingBudget_NoSpending() {
        service.setBudget(wallet, "Еда", 10000.0);
        Map<String, Double> remaining = service.getRemainingBudgets(wallet);
        assertEquals(10000.0, remaining.get("Еда"), 0.01);
    }

    @Test
    void testRemainingBudget_WithSpending() {
        service.setBudget(wallet, "Еда", 10000.0);
        service.addExpense(wallet, "Еда", 3000.0);

        Map<String, Double> remaining = service.getRemainingBudgets(wallet);
        assertEquals(7000.0, remaining.get("Еда"), 0.01);
    }

    @Test
    void testRemainingBudget_OverLimit() {
        service.setBudget(wallet, "Еда", 5000.0);
        service.addExpense(wallet, "Еда", 7000.0);

        Map<String, Double> remaining = service.getRemainingBudgets(wallet);
        assertEquals(-2000.0, remaining.get("Еда"), 0.01);
    }

    @Test
    void testValidation_CategoryCannotBeEmpty() {
        assertThrows(ValidationException.class, () -> {
            service.addIncome(wallet, "", 1000.0);
        });
    }

    @Test
    void testValidation_AmountMustBePositive() {
        assertThrows(ValidationException.class, () -> {
            service.addIncome(wallet, "Зарплата", -5000.0);
        });
    }

    @Test
    void testValidation_ZeroAmountNotAllowed() {
        assertThrows(ValidationException.class, () -> {
            service.addExpense(wallet, "Еда", 0.0);
        });
    }

    @Test
    void testGetTotalIncome_EmptyWallet() {
        assertEquals(0.0, service.getTotalIncome(wallet), 0.01);
    }

    @Test
    void testGetTotalExpenses_EmptyWallet() {
        assertEquals(0.0, service.getTotalExpenses(wallet), 0.01);
    }
}