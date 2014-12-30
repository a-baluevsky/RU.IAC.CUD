package ru.spb.iac.cud.items.app;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "appTypeClassif")
@XmlEnum
// сейчас как xsd не используется
// т.к. если использовать такие классификаторы, то везде
// в веб-сервисах передаётся просто строка
public enum AppTypeClassif {

	SYSTEM_REGISTRATION, USER_REGISTRATION, USER_ACCESS_ROLES, USER_ACCESS_GROUPS, USER_BLOCK, SYSTEM_MODIFICATION, USER_MODIFICATION, USER_MODIFICATION_ACC, USER_MODIFICATION_CERT;
}
