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
import pyodbc

print("Vamos executar o Python no SqlServer")
def sql_server_connection(server, database, username, password):
    connection_string = f"DRIVER={{SQL Server}};SERVER={'54.146.1.25'};DATABASE={'sixtracker'};UID={'sa'};PWD={'Sixtracker@'}"
    try:
        connection = pyodbc.connect(connection_string)
        return connection
    except pyodbc.Error as ex:
        print(f"Banco de dados não conectado: {ex}")
        sys.exit()


def get_ip():
    hostname = socket.gethostname()
    ip = socket.gethostbyname(hostname)
    return ip


if __name__ == "__main__":
    ip = get_ip()
    print("Ip da máquina", ip)

    SO = platform.system()
    print("Sistema operacional:", SO)

    hostname = socket.gethostname()
    print("Nome do host da máquina:", hostname)

    connection = sql_server_connection(server='54.146.1.25', database='sixtracker', username='sa',
                                       password='Sixtracker@')
    cursor = connection.cursor()

    componentes = {
        1: "Porcentagem de CPU",
        2: "Porcentagem de RAM"
    }

    cursor.execute("SELECT idComponente, nome FROM Componente WHERE fkServidor = ?", (11,))
    componentes_servidor = cursor.fetchall()

    for componente_id, componente_nome in componentes.items():
        if not any(componente_id == comp[0] for comp in componentes_servidor):
            cursor.execute("INSERT INTO Componente (nome, fkServidor) VALUES (?, ?)", (componente_nome, 11))

    if not componentes_servidor:
        print(f"Não há componentes cadastrados para o Servidor {hostname}. Cadastre componentes para continuar.")
        sys.exit()

    connection.commit()
    cursor.close()

def bytes_para_gb(bytes_value):
    return bytes_value / (1024 ** 3)

horarioAtual = datetime.now()
horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

while True:
    cpuPorcentagem = psutil.cpu_percent(None)

    memoriaPorcentagem = psutil.virtual_memory()[2]

    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime("%Y-%m-%d %H:%M:%S")

    horarioAtual = datetime.now()
    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

    ins = [cpuPorcentagem, memoriaPorcentagem]
    componentes = [264, 265]

    cursor = connection.cursor()

    for i in range(len(ins)):
        valorRegistro = ins[i]
        componente = componentes[i]

        query = "INSERT INTO Registro (valorRegistro, dataRegistro, fkComponente) VALUES (?, ?, ?)"
        cursor.execute(query, (valorRegistro, horarioAtual, componente))
        connection.commit()

    print("\n----CAPTURANDO INFORMAÇÕES DA CPU: -----")
    print(f'\nPorcentagem da CPU: {cpuPorcentagem}%')

    print("\n----CAPTURANDO INFORMAÇÕES DA MEMÓRIA: -----")
    print(f"\nPorcentagem da memória utilizada: {memoriaPorcentagem}")

    cursor.close()

    time.sleep(10)

cursor.close()
connection.close()