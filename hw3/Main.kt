class BankAccountException(message: String) : Exception(message)

class BankAccount(
    val number: Int,
    val name: String,
    var balance: Double = 0.0
)

class BankApplication {
    private val bankAccountList: MutableList<BankAccount> = mutableListOf()

    fun showAccountlist() {
        if (bankAccountList.size == 0) {
            throw BankAccountException("There are no open accounts! First create a bank account.")
        }
        bankAccountList.forEach { account ->
            println("Account ${account.number}, name: ${account.name}, balance = ${account.balance}")
        }
    }

    fun createAccount(name: String) {
        bankAccountList.add(BankAccount(bankAccountList.size + 1, name))
    }

    fun replenishAccount(number: Int, sum: Double) {
        if (bankAccountList.size == 0) {
            throw BankAccountException("There are no open accounts! First create a bank account.")
        }
        if (number < 1 || number > bankAccountList.size) {
            throw BankAccountException("There is no bank account with this number.")
        }
        bankAccountList[number - 1].balance += sum
    }

    fun moneyTransfer(numberFrom: Int, numberWhere: Int, sum: Double) {
        if (bankAccountList.size == 0) {
            throw BankAccountException("There are no open accounts! First create a bank account.")
        }
        if (numberFrom < 1 || numberFrom > bankAccountList.size || numberWhere < 1 || numberWhere > bankAccountList.size) {
            throw BankAccountException("There is no bank account with this number.")
        }
        if (sum > bankAccountList[numberFrom - 1].balance) {
            throw BankAccountException("There is not enough balance on this account.")
        }
        bankAccountList[numberWhere - 1].balance += sum
        bankAccountList[numberFrom - 1].balance -= sum
    }
}

fun printMenu() {
    println("Enter a number from 1 to 5 to:")
    println("${"\u001B[32m"}1 - view the list of accounts")
    println("2 - open a new account")
    println("3 - replenish an account")
    println("4 - transfer money between accounts")
    println("5 - exit${"\u001B[0m"}")
}

fun printRed(str: String) {
    println("${"\u001B[31m"}${str}${"\u001B[0m"}")
}

fun getInt(): Int {
    var number: Int? = readLine()?.toIntOrNull()

    while (number == null) {
        printRed("You entered an incorrect number. Please try again: ")
        number = readLine()?.toIntOrNull()
    }
    return number
}

fun main() {
    var flag: Boolean = true
    val bankApplication = BankApplication()

    while (flag) {
        printMenu()
        val num = getInt()

        try {
            if (num == 1) {
                bankApplication.showAccountlist()
            } else if (num == 2) {
                println("Enter the name for the new account:")
                var str: String? = readLine()

                while (str.isNullOrEmpty()) {
                    printRed("You entered an empty string. Please try again: ")
                    str = readLine()
                }

                bankApplication.createAccount(str);
            } else if (num == 3) {
                println("Enter the account number:")
                val number = getInt()

                println("Enter the replenishment amount:")
                var sum: Double? = readLine()?.toDoubleOrNull()

                while (sum == null) {
                    printRed("You entered an incorrect replenishment amount. Please try again: ")
                    sum = readLine()?.toDoubleOrNull()
                }

                bankApplication.replenishAccount(number, sum)
            } else if (num == 4) {
                println("Enter the account number to withdraw funds from:")
                val numFrom: Int = getInt()

                println("Enter the account number to transfer funds to:")
                val numWhere: Int = getInt()

                println("Enter the transfer amount:")
                var sum: Double? = readLine()?.toDoubleOrNull()

                while (sum == null) {
                    printRed("You entered an incorrect transfer amount. Please try again: ")
                    sum = readLine()?.toDoubleOrNull()
                }

                bankApplication.moneyTransfer(numFrom, numWhere, sum)
            } else if (num == 5) {
                flag = false
            } else {
                printRed("You did not enter a value from 1 to 5!")
            }
        } catch (e: BankAccountException) {
            printRed("${e.message}")
        }
    }
}