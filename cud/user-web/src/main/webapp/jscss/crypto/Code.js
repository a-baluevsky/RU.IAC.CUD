


function setPage() {
/*
alert('setPage');

    var hrefString = document.location.href ? document.location.href : document.location;
    if (document.getElementById("nav") != null)
        setActiveMenu(document.getElementById("nav").getElementsByTagName("a"), extractPageName(hrefString));*/
}

function ObjCreator(name, BrowserName) {

//alert('ObjCreator');

    switch (document.getElementById(BrowserName).value) {
        case 'Microsoft Internet Explorer':
            return new ActiveXObject(name);           
        default:
	    var userAgent = navigator.userAgent;
            if(userAgent.match(/ipod/i) ||userAgent.match(/ipad/i) || userAgent.match(/iphone/i)) {
             return call_ru_cryptopro_npcades_10_native_bridge("CreateObject", [name]);
            }
            var cadesobject = document.getElementById('cadesplugin');
            return cadesobject.CreateObject(name);
            
    }
}

function SignBtn_Click(lstid, browserName, dataID, TSPid, SignId, SignTypeId) {

    var e = document.getElementById(lstid);
    var selectedCertID = e.selectedIndex;
    if (selectedCertID == -1) {
        alert("Не выбран сертификат!");
        return;
    }

    var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
    try {
        var oStore = ObjCreator("CAPICOM.store", browserName);
        oStore.Open();
    } catch (err) {
        alert('Ошибка чтения хранилища сертификатов: ' + err.number);
        return;
    }

    var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;    
    var oCerts = oStore.Certificates.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

    if (oCerts.Count == 0) {
        alert("Сертификат отсутствует");
        return;
    }
    var oCert = oCerts.Item(1);
    try {
        var oSigner = ObjCreator("CAdESCOM.CPSigner", browserName);        
    } catch (err) {
        alert('Ошибка чтения CPSigner: ' + errerr.number);
        return;
    }
    if (oSigner) {
        oSigner.Certificate = oCert;
    }
    else {
        alert("Ошибка оздания CPSigner");
        return;
    }
        
    var oSignedData = ObjCreator("CAdESCOM.CadesSignedData", browserName);

    var txtDataToSign = "12345";//document.getElementById(dataID).value;

    var CADES_BES = 1;
    var CADESCOM_CADES_DEFAULT = 0;
    //try {
        if (txtDataToSign) {
            // Данные на подпись ввели
            oSignedData.Content = txtDataToSign;
            //Выбираем тип подписи
           // if (document.getElementById('RdBtn1').checked == true) {


                document.getElementById(SignTypeId).value = 0;
                oSigner.Options = 1; //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                try {
                /*	var _result = "";
                	
                    for (var _i in oSignedData) {
                    	 try {
                        _result +=  _i + " = " + oSignedData[_i] + "\n";
                    	 }catch (e2) {
                    		// alert("e2:"+e2);
                    	 }
                     }
                    alert(_result);*/
                	
                    var sSignedData = oSignedData.SignCades(oSigner, CADES_BES);
     
                }catch (e) {
                 //   alert("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(e));
                    alert("Выбранный сертификат не корректен!\n" +
                    	  "Выберите другой сертификат!");
                    return;
                }

//                var sSignedData = oSignedData.Sign(oSigner, false, 0);
                document.getElementById(SignId).value = sSignedData;
         /*   } else {
                var txtTSPaddr = document.getElementById(TSPid).value;
                if (txtTSPaddr) {
                    document.getElementById(SignTypeId).value = 1;
                    oSigner.TSAAddress = txtTSPaddr;
                    try {
                        var sSignedData = oSignedData.SignCades(oSigner, CADESCOM_CADES_DEFAULT);
                    }
                    catch (e) {
                        alert("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(e));
                        return;
                    }
                    document.getElementById(SignId).value = sSignedData;
                } else {
                    alert("Enter TSP address");
                }
          }*/
        }
//    } catch (e) {
//        alert("Failed to create signature: " + e.number);
//        return;
//    }
    oStore.Close();

    document.forms[0].submit();
}

function SignatureTypeBtnClick(value, txtBoxID) {
    switch (value) {
        case "1":
            document.getElementById(txtBoxID).disabled = true;
            break;
        case "2":
            document.getElementById(txtBoxID).disabled = false;
            break;
        default:
            alert("Radio button unknown value");
    }
}


function SignatureTypeBtnClick(value, txtBoxID) {

/*  alert('SignatureTypeBtnClick');

    switch (value) {
        case "1":
            document.getElementById(txtBoxID).disabled = true;
            break;
        case "2":
            document.getElementById(txtBoxID).disabled = false;
            break;
        default:
            alert("Radio button unknown value");
    }*/
}

function CheckForPlugIn(id1, id2) {

// alert('CheckForPlugIn');

    document.getElementById(id1).setAttribute("value", "0");
    document.getElementById(id2).setAttribute("value", navigator.appName);
    switch (navigator.appName) {
        case 'Microsoft Internet Explorer':
            try {
                var obj = new ActiveXObject("CAdESCOM.CPSigner");
                document.getElementById(id1).setAttribute("value", "1");
            }
            catch (err) {
            }
            break;
        //case 'Netscape':    
        default:
	    var userAgent = navigator.userAgent;
	    if(userAgent.match(/ipod/i) ||userAgent.match(/ipad/i) || userAgent.match(/iphone/i)) {
		document.getElementById(id1).setAttribute("value", "1");
                document.write("<object id=\"cadesplugin\" type=\"application/x-cades\" class=\"hiddenObject\"></object>");
		//alert(userAgent);
		break;
            }
            var cadesobject = document.getElementById('FFembeded');
            var mimetype = navigator.mimeTypes["application/x-cades"];
            if (mimetype) {
                var plugin = mimetype.enabledPlugin;
                if (plugin) {
                    document.getElementById(id1).setAttribute("value", "1");
                   document.write("<object id=\"cadesplugin\" type=\"application/x-cades\" class=\"hiddenObject\"></object>");                    
                }
            }
    }

    if (document.getElementById(id1).value == "1") {
       // document.getElementById('PluginEnabledImg').setAttribute("src", "Img/green_dot.png");
        document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен";
        document.getElementById('PlugInEnabledTxt').style.color = "green";
        document.getElementById('PlugInEnabledTxtDownload').style.display = "none";
    } else {
       // document.getElementById('PluginEnabledImg').setAttribute("src", "Img/red_dot.png");
        document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин не загружен";
        document.getElementById('PlugInEnabledTxt').style.color = "red";
        document.getElementById('PlugInEnabledTxtDownload').style.display = "inline";
        
    }
}

function CheckServerResponce(signVal) {
/*
alert('CheckServerResponce');


    if (document.getElementById(signVal).value != "") {
        document.write("<span style=\"font-family: Tahoma\"><span style=\"font-size: 11pt;\">Ответ сервера</span>");
        document.write("<hr width=\"115px\" align=\"left\" />");
        document.write("<div class=\"controlLayout\">");
        document.write(document.getElementById(signVal).value);
        document.write("</div>");

        document.getElementById(signVal).value = "";
    }*/
}
function FillCertList(lstId, BrowserName) {


    var oStore = ObjCreator("CAPICOM.store", BrowserName);
    if (!oStore) {
        alert("Ошибка чтения хранилища сертификатов");
        return;
    }

    try {
        oStore.Open();
    }
    catch (e) {
        alert("Ошибка при открытии хранилища: " + GetErrorMessage(e));
        return;
    }

    var certCnt = oStore.Certificates.Count;

    var lst = document.getElementById(lstId);   
    
      
    for (var i = 1; i <= certCnt; i++) {
        var cert;
        try {
            cert = oStore.Certificates.Item(i);
//alert('cert.SubjectName:'+cert.SerialNumber);
        //  alert('cert.IssuerName:'+cert.IssuerName);
          /*  var result = "";
            for (var i in oStore.Certificates) {
                result +=  i + " = " + oStore.Certificates[i] + "\n";
             }
            alert(result);*/
        }
        catch (ex) {
            alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
            return;
        }

        var oOpt = document.createElement("OPTION");
        try {
            oOpt.text = cert.SubjectName;
        }
        catch (e) {
            alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(e));
        }
        try {
            oOpt.value = cert.Thumbprint;
        }
        catch (e) {
            alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(e));
        }

        lst.options.add(oOpt);
    }

    oStore.Close();
}

function decimalToHexString(number)
{
    if (number < 0) {
        number = 0xFFFFFFFF + number + 1;
    }

    return number.toString(16).toUpperCase();
}

function GetErrorMessage(e)
{
    var err = e.message;
    if (!err) {
        err = e;
    } else if (e.number) {
        err += " (0x" + decimalToHexString(e.number) + ")";
    }    
    return err;
}
