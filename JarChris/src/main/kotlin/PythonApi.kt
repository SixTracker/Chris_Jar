import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.reflect.typeOf

object PythonApi {
    private lateinit var processoPython: Process
    private lateinit var errorStream: InputStreamReader
    private lateinit var errorBufferedReader: BufferedReader

    fun chamarApiPython(idServidor: Int, fkComponenteCPU: Int, fkComponenteRAM: Int) {
        val nomeArquivoPyDefault = "py_kotlin.py"
        val codigoPython = """
from mysql.connector import connect
import psutil
import platform
import time
import mysql.connector
from datetime import datetime
import requests
import random
import json
import string
import socket
import sys


print("Script Python em execução...")


def mysql_connection(host, user, passwd, database=None):
    connection = connect(
        host=host,
        user=user,
        passwd=passwd,
        database=database
    )
    return connection


def get_ip():
    hostname = socket.gethostname()
    ip = socket.gethostbyname(hostname)
    return ip


if __name__ == "__main__":
    ip = get_ip()
    print("O ip da máquina é:", ip)

    SO = platform.system()
    print("O sistema operacional é:", SO)

    hostname = socket.gethostname()
    print("Nome do host da máquina:", hostname)

    connection = mysql_connection('localhost', 'root', '1234', 'sixtracker')
    cursor = connection.cursor()

    # Definindo os componentes
    componentes = {
        1: "Porcentagem de Memória",
        2: "Total de Memória",
        3: "Memória Usada",
        4: "Porcentagem de Memória Swap",
        5: "Memória Swap Usada"
    }

    # Buscar os componentes cadastrados para o servidor
    cursor.execute("SELECT idComponente, nome FROM Componente WHERE fkServidor = %s", ($idServidor,))    
    componentes_servidor = cursor.fetchall()

    # Verificar e adicionar os componentes de 1 a 5 se não existirem
    for componente_id, componente_nome in componentes.items():
        if not any(componente_id == comp[0] for comp in componentes_servidor):
            # Componente não encontrado, adicionar à tabela Componente
            cursor.execute("INSERT INTO Componente (nome, fkServidor) VALUES (%s, $idServidor)", (componente_nome,))

    if not componentes_servidor:
        print(f"Não há componentes cadastrados para o Servidor {hostname}. Cadastre componentes para continuar.")
        sys.exit()

    connection.commit()
    cursor.close()


def bytes_para_gb(bytes_value):
    return bytes_value / (1024 ** 3)


horarioAtual = datetime.now()
horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

# Seu código anterior...

while True:
    # CPU
    cpuPorcentagem = psutil.cpu_percent(None)

    # Memoria
    memoriaPorcentagem = psutil.virtual_memory()[2]

    # Outros
    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime("%Y-%m-%d %H:%M:%S")

    horarioAtual = datetime.now()
    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

    ins = [cpuPorcentagem, memoriaPorcentagem]
    componentes = [$fkComponenteCPU, $fkComponenteRAM]

    cursor = connection.cursor()

    for i in range(len(ins)):
        valorRegistro = ins[i]
        componente = componentes[i]

        query = "INSERT INTO Registro (valorRegistro, dataRegistro, fkComponente) VALUES (%s, %s, %s)"
        cursor.execute(query, (valorRegistro, horarioFormatado, componente))
        connection.commit()

    print("\n----INFORMAÇÕES DA CPU: -----")
    print(f'\nPorcentagem da CPU: {cpuPorcentagem}%')

    print("\n----INFORMAÇÕES DA MEMORIA: -----")
    print(f"\nPorcentagem utilizada de memoria: {memoriaPorcentagem}")

    cursor.close()

    # Este sleep deve estar dentro do loop
    time.sleep(10)

cursor.close()
connection.close()

        """.trimIndent()

        File(nomeArquivoPyDefault).writeText(codigoPython)

        // Use ProcessBuilder para iniciar o processo Python
        println("Iniciando o processo Python...")
        val processBuilder = ProcessBuilder("python.exe", nomeArquivoPyDefault, idServidor.toString())
        processoPython = processBuilder.start()
        println("Processo Python iniciado.")


        // Inicialize as propriedades relacionadas
        errorStream = InputStreamReader(processoPython.errorStream)
        errorBufferedReader = BufferedReader(errorStream)

        val thread = Thread {
            val combinedInputStream = InputStreamReader(processoPython.inputStream)
            val combinedBufferedReader = BufferedReader(combinedInputStream)

            val errorInputStream = InputStreamReader(processoPython.errorStream)
            val errorBufferedReader = BufferedReader(errorInputStream)

            while (true) {
                // Leia a saída padrão
                val line = combinedBufferedReader.readLine()
                if (line == null) break
                println(line)

                // Leia a saída de erro
                val errorLine = errorBufferedReader.readLine()
                if (errorLine == null) break
                println("Erro: $errorLine")
            }
        }

        thread.start()

    }




}
