package fr.ebiz.computerdatabase.ui.cli;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.CompanyServiceImpl;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;
import fr.ebiz.computerdatabase.ui.cli.printer.factory.PrettyPrintFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Cli {
    public static final int DEFAULT_PAGE = 1;
    private static final int MAX_ELEMENTS = 20;
    private ComputerService computerService;
    private CompanyService companyService;

    private Cli() {
        this.computerService = ComputerServiceImpl.getInstance();
        this.companyService = CompanyServiceImpl.getInstance();
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
                        .make(Company.class).printList(companyService.getAll(Pageable.builder().page(DEFAULT_PAGE).elements(Integer.MAX_VALUE).build()).getElements()));
                break;
            case LIST_COMPUTERS:
                listComputers(scanner);
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

    private void listComputers(Scanner scanner) {
        int currentPage = DEFAULT_PAGE;
        Page<Computer> page = null;

        System.out.println("Here is the list of computers");
        do {
            if (page == null || page.getCurrentPage() != currentPage) {
                page = computerService.getAll(Pageable.builder().page(currentPage).elements(MAX_ELEMENTS).build());
            }
            System.out.println(PrettyPrintFactory.getInstance()
                    .make(Computer.class).printList(page.getElements()));

            System.out.print("Choose a page between 1 and " + page.getTotalPages() + " or \"quit\" to go back : ");
            String input = scanner.nextLine();
            if (Command.QUIT.getCommandString().equals(StringUtils.cleanString(input))) {
                return;
            } else {
                if (StringUtils.isNumeric(input)) {
                    int readPage = Integer.parseInt(input);
                    if (readPage < 1 || readPage > page.getTotalPages()) {
                        System.out.println("Page is out of range. It must be between 1 and " + page.getTotalPages());
                    } else {
                        currentPage = readPage;
                    }
                } else {
                    System.out.println("Page must be an integer !");
                }
            }
        } while(true);
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

        Computer.ComputerBuilder computerBuilder = Computer.builder()
                .name(PrinterUtils.readString(scanner,
                "Name of the computer* : ",
                        true))
                .introduced(PrinterUtils.readDate(scanner,
                "Introduced in (dd/mm/yyyy) or empty (no date) : ",
                        false))
                .discontinued(PrinterUtils.readDate(scanner,
                "Discontinued in (dd/mm/yyyy) or empty (no date) : ",
                false));

        Company company = readCompany(scanner, false);
        Computer computer = computerBuilder.companyId(company == null ? null : company.getId()).build();
        computerService.insert(computer);
        System.out.println(computer.toString() + " was inserted successfully");
    }

    private void updateComputer(Scanner scanner) {
        Computer computer = readComputer(scanner,
                "Id of the computer to update : ");

        Computer.ComputerBuilder computerBuilder = Computer.builder()
                .id(computer.getId())
                .name(PrinterUtils.readString(scanner,
                "Name of the computer* [" + computer.getName() + "]: ",
                        true))
                .introduced(PrinterUtils.readDate(scanner,
                "Introduced in (dd/mm/yyyy) or empty (no date) [" + computer.getIntroduced() + "] : ",
                        false))
                .discontinued(PrinterUtils.readDate(scanner,
                "Discontinued in (dd/mm/yyyy) or empty (no date) [" + computer.getDiscontinued() + "] : ",
                false));

        Company company = readCompany(scanner, false);

        Computer computerToUpdate = computerBuilder.companyId(company == null ? null : company.getId()).build();
        computerService.update(computerToUpdate);
        System.out.println(computerToUpdate.toString() + " was updated successfully");
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
                Optional<Computer> computer = computerService.get(computerId);
                if (computer.isPresent()) {
                    return computer.get();
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
                    Optional<Company> company = companyService.get(companyId);
                    if (company.isPresent()) {
                        return company.get();
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