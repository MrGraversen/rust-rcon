package io.graversen.rust.rcon.objects.rust;

import com.google.common.base.CaseFormat;
import io.graversen.rust.rcon.objects.util.Animals;
import io.graversen.rust.rcon.objects.util.Population;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class SpawnReport
{
    private final Map<String, Population> rawReport;

    public SpawnReport(Map<String, Population> rawReport)
    {
        this.rawReport = rawReport;
    }

    public Map<String, Population> getRawReport()
    {
        return rawReport;
    }

    public Population getPopulationByAnimal(Animals animals)
    {
        final var capitalizedAnimal = StringUtils.capitalize(animals.name().toLowerCase());
        return rawReport.getOrDefault(String.format("%s.Population", capitalizedAnimal), null);
    }
}
