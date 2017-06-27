package fr.ebiz.computerdatabase.ui;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.CompanyServiceImpl;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.ComputerServiceImpl;
import fr.ebiz.computerdatabase.ui.printer.factory.PrettyPrintFactory;

import java.util.Arrays;
import java.util.Scanner;

public class Cli {
    private ComputerService computerService;
    private CompanyService companyService;

    private Cli() {
        this.computerService = new ComputerServiceImpl();
        this.companyService = new CompanyServiceImpl();
    }

    public static void main(String... args) {
        new Cli().start();
    }

    private void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("\nThanks for using the Computer Database CLI application")));
        Scanner scanner = new Scanner(System.in);
        Command command;

        System.out.println("Hello, welcome to the Computer Database CLI application");
        help();

        do {
            System.out.print("\nAction : ");
            command = Command.getCommand(scanner.nextLine());

            if (command != null) {
                handleCommand(scanner, command);
            } else {
                System.out.println("Invalid command. Please refer to the \"help\" command");
            }

        } while (Command.QUIT != command);
    }

    private void handleCommand(Scanner scanner, Command command) {
        switch (command) {
            default:
            case HELP:
                help();
                break;
            case QUIT:
                System.exit(0);
            case LIST_COMPANIES:
                System.out.println("Here is the list of companies");
                System.out.println(PrettyPrintFactory.getInstance()
                        .make(Company.class).printList(companyService.getAll()));
                break;
            case LIST_COMPUTERS:
                System.out.println("Here is the list of computers");
                System.out.println(PrettyPrintFactory.getInstance()
                        .make(Computer.class).printList(computerService.getAll()));
                break;
            case SHOW_COMPUTER:
                showComputerDetail(scanner);
                break;
            case ADD_COMPUTER:
                addComputer(scanner);
                break;
            case UPDATE_COMPUTER:
                updateComputer(scanner);
                break;
            case DELETE_COMPUTER:
                deleteComputer(scanner);
                break;
        }
    }

    private void help() {
        System.out.println("Here is the list of command you can use :");
        Arrays.stream(Command.values()).forEach(command ->
                System.out.println("\t" + command.getCommandString() + " : " + command.getHelpMessage()));
    }


    private void showComputerDetail(Scanner scanner) {
        Computer computer = readComputer(scanner, "Id of the computer to show : ");
        System.out.println(PrettyPrintFactory.getInstance().make(Computer.class).printDetail(computer));
    }


    private void addComputer(Scanner scanner) {
        Computer computer = new Computer();

        computer.setName(PrinterUtils.readString(scanner, "Name of the computer* : ", true));
        computer.setIntroduced(PrinterUtils.readLocalDate(scanner, "Introduced in (dd/mm/yyyy) or empty (no date) : ", false));
        computer.setDiscontinued(PrinterUtils.readLocalDate(scanner, "Discontinued in (dd/mm/yyyy) or empty (no date) : ", false));

        Company company = readCompany(scanner, false);
        computer.setCompanyId(company == null ? null : company.getId());

        computerService.insert(computer);
        System.out.println(computer.toString() + " was inserted successfully");
    }

    private void updateComputer(Scanner scanner) {
        Computer computer = readComputer(scanner, "Id of the computer to update : ");

        computer.setName(PrinterUtils.readString(scanner, "Name of the computer* [" + computer.getName() + "]: ", true));
        computer.setIntroduced(PrinterUtils.readLocalDate(scanner,
                "Introduced in (dd/mm/yyyy) or empty (no date) [" + computer.getIntroduced() + "] : ",
                false));
        computer.setDiscontinued(PrinterUtils.readLocalDate(scanner,
                "Discontinued in (dd/mm/yyyy) or empty (no date) [" + computer.getDiscontinued() + "] : ",
                false));

        Company company = readCompany(scanner, false);
        computer.setCompanyId(company == null ? null : company.getId());

        computerService.update(computer);
        System.out.println(computer.toString() + " was updated successfully");
    }

    private void deleteComputer(Scanner scanner) {
        Computer computer = readComputer(scanner, "Id of the computer to delete : ");
        computerService.delete(computer);
        System.out.println(computer.toString() + " was deleted successfully");
    }

    private Computer readComputer(Scanner scanner, String label) {
        do {
            System.out.print(label);
            String input = scanner.nextLine();
            if (StringUtils.isNumeric(input)) {
                int computerId = Integer.parseInt(input);
                Computer computer = computerService.get(computerId);
                if (computer != null) {
                    return computer;
                } else {
                    System.out.println("Computer does not exist !");
                }
            } else {
                System.out.println("Invalid computer id !");
            }
        } while (true);
    }

    private Company readCompany(Scanner scanner, boolean mandatory) {
        do {
            System.out.print("Company id : ");
            String input = scanner.nextLine();

            if (mandatory && StringUtils.isBlank(input)) {
                System.out.println("Company id is empty !");
            } else if (!mandatory && StringUtils.isBlank(input)) {
                // We accept null because it's not mandatory
                return null;
            } else {
                if (StringUtils.isNumeric(input)) {
                    int companyId = Integer.parseInt(input);
                    Company company = companyService.get(companyId);
                    if (company != null) {
                        return company;
                    } else {
                        System.out.println("Company does not exist !");
                    }
                } else {
                    System.out.println("Invalid company id !");
                }
            }
        } while (true);
    }

}