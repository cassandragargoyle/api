/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.software.SoftwareFeatures;
import org.cassandragargoyle.api.software.SoftwareCategory;
import java.util.List;
import java.util.Map;
import org.cassandragargoyle.api.software.CodeLanguage;
import org.cassandragargoyle.api.software.OSType;

/**
 * Interface SoftwareManager
 * Existuje mnoho anglických výrazů, které se používají pro software. Tyto termíny se mohou lišit v závislosti na kontextu nebo na konkrétní oblasti použití. Zde je přehled běžných výrazů:
 * Software – obecný termín pro počítačové programy.
 * Application – program zaměřený na konkrétní úlohu nebo potřebu uživatele (např. textový procesor, webový prohlížeč).
 * Program – sada instrukcí, které vykonávají konkrétní úlohu.
 * App – zkrácenina pro "application", často používaná pro mobilní aplikace.
 * System Software – software, který zajišťuje běh hardwaru a základní funkce počítače (např. operační systém).
 * Utility – malý program, který provádí jednoduchou úlohu (např. správce souborů, antivirový program).
 * Tool – nástroj, který slouží k určitému technickému účelu (např. vývojářské nástroje).
 * Platform – softwarové prostředí, ve kterém běží aplikace (např. Windows, Linux).
 * Framework – předdefinovaná sada knihoven a pravidel pro usnadnění vývoje aplikací (např. .NET, Django).
 * Library – sada předem napsaných kódů a funkcí, které mohou být použity v různých programech (např. Boost, jQuery).
 * Module – část programu, která provádí konkrétní úlohu a je oddělitelná od zbytku systému.
 * Executable – spustitelný soubor, který může být přímo proveden počítačem (např. .exe soubory).
 * Script – kratší program, který se obvykle provádí v interpreteru (např. Python, Bash).
 * Middleware – software, který zprostředkovává komunikaci mezi jinými programy nebo částmi systému.
 * Firmware – speciální software, který je uložen na hardwarovém zařízení a umožňuje jeho funkčnost.
 * Suite – sada vzájemně propojených aplikací (např. Microsoft Office Suite).
 * Engine – software, který provádí specifické úlohy, často ve hrách nebo databázích (např. herní engine, databázový engine).
 * Component – část softwaru, kterou lze použít v různých kontextech.
 * Plugin / Add-on – rozšíření nebo doplněk, který přidává nové funkce do existujícího softwaru.
 * Beta / Alpha Software – předběžné verze softwaru používané k testování před finálním vydáním.
 * Prototype – první verze softwaru, která slouží k testování konceptu.
 * Client / Server Software – software, který komunikuje v klient-server architektuře.
 *
 * @author kurc
 * @since 2024-11-05
 */
public interface Software extends Entity
{
	/**
	 * Check if is software instaled on local platform (operating system, image or container).
	 * @return
	 */
	boolean isInstalled(Object checkMethod);

	public void install();

	public List<Version> getVersions();

	SoftwareFeatures[] getFeatures();

	SoftwareCategory[] getCategories();

	OSType[] getSupportedOperatingSystems();

	Map<String, String> getSourceCodeUrl();

	String getInstallScript(Platform platform, CodeLanguage language);
}
