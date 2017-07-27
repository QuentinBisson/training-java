package fr.ebiz.computerdatabase.ui.web.computers;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;
import fr.ebiz.computerdatabase.ui.web.Form;
import fr.ebiz.computerdatabase.utils.FormUtils;
import fr.ebiz.computerdatabase.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ComputerForm implements Form<ComputerDto> {

    private ComputerDto model;

    @Override
    public Map<String, String> parseAndValidate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ComputerDto.ComputerDtoBuilder builder = ComputerDto.builder();

        Map<String, String> errors = new HashMap<>();

        String idParam = request.getParameter(ComputerValidator.COMPUTER_ID_FIELD);
        if (StringUtils.isNumeric(idParam)) {
            builder.id(Integer.parseInt(idParam));
        } else if (!StringUtils.isBlank(idParam)) {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        builder.name(request.getParameter(ComputerValidator.COMPUTER_NAME_FIELD));

        String introducedParam = request.getParameter(ComputerValidator.COMPUTER_INTRODUCED_FIELD);
        LocalDate introduced = FormUtils.getLocalDate(introducedParam);
        if (!StringUtils.isBlank(introducedParam) && introduced != null) {
            builder.introduced(introduced);
        } else if (!StringUtils.isBlank(introducedParam)) {
            errors.put(ComputerValidator.COMPUTER_INTRODUCED_FIELD, "Introduction is not a valid date !");
        }

        String discontinuedParam = request.getParameter(ComputerValidator.COMPUTER_DISCONTINUED_FIELD);
        LocalDate discontinued = FormUtils.getLocalDate(discontinuedParam);
        if (!StringUtils.isBlank(discontinuedParam) && discontinued != null) {
            builder.discontinued(discontinued);
        } else if (!StringUtils.isBlank(discontinuedParam)) {
            errors.put(ComputerValidator.COMPUTER_DISCONTINUED_FIELD, "Discontinuation is not a valid date !");
        }

        String companyParam = request.getParameter(ComputerValidator.COMPUTER_COMPANY_FIELD);
        if (StringUtils.isNumeric(companyParam)) {
            builder.companyId(Integer.parseInt(companyParam));
        } else if (!StringUtils.isBlank(companyParam)) {
            errors.put(ComputerValidator.COMPUTER_COMPANY_FIELD, "Company id is not valid !");
        }

        model = builder.build();
        return errors;
    }

    public ComputerDto getModel() {
        return model;
    }
}