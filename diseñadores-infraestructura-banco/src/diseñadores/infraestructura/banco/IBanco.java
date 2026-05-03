package diseñadores.infraestructura.banco;

import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

interface IBanco {

  RespuestaBancoDTO procesarTarjeta(SolicitudBancoDTO solicitud);

  RespuestaBancoDTO procesarTransferencia(SolicitudBancoDTO solicitud);

  RespuestaBancoDTO procesarQR(SolicitudBancoDTO solicitud);

}
