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

print("Iniciando Script de python")
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
    print("Ip da máquina:", ip)

    SO = platform.system()
    print("Sistema operacional:", SO)

    hostname = socket.gethostname()
    print("Nome do host da máquina:", hostname)

    connection = mysql_connection('localhost', 'root', '1234', 'sixtracker')
    cursor = connection.cursor()

    componentes = {
        1: "Porcentagem de Memória",
        2: "Total de Memória",
        3: "Memória Usada",
        4: "Porcentagem de Memória Swap",
        5: "Memória Swap Usada"
    }

    cursor.execute("SELECT idComponente, nome FROM Componente WHERE fkServidor = %s", (426,))    
    componentes_servidor = cursor.fetchall()

    for componente_id, componente_nome in componentes.items():
        if not any(componente_id == comp[0] for comp in componentes_servidor):
            cursor.execute("INSERT INTO Componente (nome, fkServidor) VALUES (%s, 426)", (componente_nome,))

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
    componentes = [181, 182]

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
    time.sleep(10)

cursor.close()
connection.close()
