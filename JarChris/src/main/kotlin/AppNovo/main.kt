import javax.swing.JOptionPane
import javax.swing.JOptionPane.*

fun main() {
    val py = PythonApi
    val pyServer = PythonApiSqlServer
    val login = Usuario1()
    login.iniciar()

    login.email = showInputDialog("Seu email:").toString()
    login.senha = showInputDialog("Sua senha:").toString()

    if (login.validarLogin(login)) {
        showMessageDialog(null, login.entrando(login))
        val fkEmpresa = login.verificarEmpresa(login)
        val listaDeServidor = fkEmpresa?.let { login.mostrarServidor(it) }
        showConfirmDialog(null, "Vamos começar o monitoramento!")
        val idServidor =
            showInputDialog("Digite o ID do servidor que você deseja monitorar:\n\r $listaDeServidor")
                .toInt()

        val listaDeComponente = login.mostrarComponentes(idServidor)
        val fkComponenteCPU =
            showInputDialog("Digite o ID do componente  de cpu que você deseja monitorar:\n\r $listaDeComponente")
                .toInt()
        val fkComponenteRAM =
            showInputDialog("Digite o ID do componente de ram que você deseja monitorar:\n\r $listaDeComponente")
                .toInt()


        pyServer.chamarApiPython(idServidor, fkComponenteCPU, fkComponenteRAM)
    }

}
