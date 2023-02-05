<template>
  <v-container>
    <v-row>
      <v-col cols="7">

<!--        <searchBar></searchBar>-->

        <v-divider></v-divider>
        <!--selected user -->
        <v-fade-transition>
          <div v-if="selectedUser!=null" class="py-2" >
            <v-row>
              <v-col cols="9">
                <div class="pa-2 text-h6">
                  Selectionné : {{ selectedUser.firstName }} {{ selectedUser.lastName }}
                </div>
              </v-col>

              <v-col cols="1">
                <v-icon icon="mdi-close" @click="resetSelectedRows" ></v-icon>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="4" v-if="can('advisor')">
                <AccountCreationModal :user="selectedUser" @resetComptes="getComptes"></AccountCreationModal>
              </v-col>

            </v-row>

          </div>
          </v-fade-transition>

            <v-divider></v-divider>
            <!--tableau utilisateur-->
            <div class="text-h6"> Utilisateur</div>
            <EasyDataTable
                theme-color="#1d90ff"
                table-class-name="customize-table"
                :headers="headers"
                :items="itemsUserTable"
                :rows-per-page="5"
                @click-row="selectUser">

              <template #item-actions="item" v-if="can('advisor')">
                <div class="operation-wrapper">
                  <!--deleteItem(item)-->
                  <v-icon icon="mdi-delete"  @click="openModalDeleteUser(item)"></v-icon>
                </div>
              </template>

            </EasyDataTable>
            <v-divider></v-divider>

            <!--tableau compte-->
            <div class="text-h6"> Compte</div>
            <EasyDataTable
                theme-color="#1d90ff"
                table-class-name="customize-table"
                :headers="headersCompte"
                :items="itemsCompteTable"
                :rows-per-page="10"
                @click-row="selectCompte"
            >

              <template #item-actions="item" v-if="can('advisor')">
                <div class="operation-wrapper">
                  <v-icon icon="mdi-delete"  @click="openModalDeleteCompte(item)"></v-icon>
                </div>
              </template>

            </EasyDataTable>

      </v-col>
      <!--tableau transaction-->
      <v-col cols="5">


        <div class="text-h6"> Transaction</div>


        <v-fade-transition>
          <div v-if="selectedCompte!=null" class="py-2" >
            <v-row
                align="center"
                justify="center">
              <v-col cols="6">
                <div class="pa-2 text-h6">
                  N°Compte : {{ selectedCompte.id }}
                </div>
              </v-col>

              <v-col cols="4">
                <ChequeRequest :compte="selectedCompte"></ChequeRequest>
              </v-col>

              <v-col cols="1">
                <v-icon icon="mdi-close" @click="resetSelectedRows" ></v-icon>
              </v-col>

            </v-row>
          </div>
        </v-fade-transition>

        <v-fade-transition>
          <EasyDataTable
              theme-color="#1d90ff"
              table-class-name="customize-table"
              :headers="headersTransaction"
              :items="itemsTransactionTable"
              :rows-per-page="10"

              v-if="(itemsTransactionTable.length!==0)"
          >
          </EasyDataTable>

          <div v-else>
            <v-banner
                lines="one"
                icon="mdi-checkbook"
                color="deep-purple-accent-4"
                class="my-4"
            >
              <v-banner-text>
                Selectionner un compte pour voir les transactions
              </v-banner-text>
            </v-banner>

            <div class="text-h5 text-grey-lighten-1 text-center" v-if="!loadingTransaction">Vide</div>
            <div class="text-center">
              <v-progress-circular
                  indeterminate
                  color="primary"
                  v-if="loadingTransaction"
              ></v-progress-circular>
            </div>

          </div>
        </v-fade-transition>

      </v-col>
    </v-row>


    <!--modal for delete user-->
    <v-dialog
        v-model="dialogDeleteUser"
        max-width="600"
    >
      <v-card>
        <v-toolbar
            color="primary">
          <v-toolbar-title>Suppression de l'utilisateur  {{ selectedUser.nom }}</v-toolbar-title>
        </v-toolbar>

        <v-card-text>
          Vous etes sur le point de supprimer l'utilisateur {{selectedUser.nom}}, etes vous sur de supprimer cette utilisateur.
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey"  @click="dialogDeleteUser = false">Annuler</v-btn>
          <v-btn
              color="danger"
              variant="text"
              @click="deleteUser(selectedUser)"
          >
            Supprimer
          </v-btn>

        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--modal for delete compte-->
    <v-dialog
        v-model="dialogDeleteCompte"
        max-width="600"
    >
      <v-card>
        <v-toolbar
            color="primary"

        >
          <v-toolbar-title>Suppression du compte {{ selectedCompte.id_compte }}</v-toolbar-title>
        </v-toolbar>


        <v-card-text>
          Vous etes sur le point de supprimer le compte  {{selectedCompte.id_compte}}, etes vous sur de supprimer ce compte.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="grey"  @click="dialogDeleteCompte = false">Annuler</v-btn>
          <v-btn
              color="danger"
              variant="text"
              @click="deleteCompte(selectedCompte)"
          >
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>

</template>


<script>

import { useTheme } from 'vuetify'
import { useToast } from "vue-toastification";
// import searchBar from '@/components/SearchBar'
import Vue3EasyDataTable from 'vue3-easy-data-table'
import authHeader from "@/services/auth-header";
import {can} from "@/utils"
import AccountCreationModal from "@/components/AccountCreationModal";
import ChequeRequest from "@/components/ChequeRequest";


export default {
  setup () {
    const theme = useTheme();
    const toast = useToast();
    const headers = [
      { text: "Identifiant", value: "userId" },
      { text: "NOM", value: "lastName"},
      { text: "PRENOM", value: "firstName"},

      { text: "ACTIONS", value: "actions"},
    ];
    const headersCompte = [
      { text: "N°Compte", value: "id" },
      { text: "Titre", value: "title" },
      { text: "Détenteur", value: "owner" },
      { text: "Conseiller", value: "advisor"},
      { text: "solde", value: "balance"},
      { text: "ACTIONS", value: "actions"},
    ];
    const headersTransaction = [
      { text: "N° Transaction", value: "id_transaction" },
      { text: "Date", value: "date"},
      { text: "Compte source", value: "source"},
      { text: "Compte destination", value: "destination"},
      { text: "Montant", value: "amount"},
    ];

    //TODO pour le moment static mais sera remplacer par une requete qui recupere les transactions
    const itemsTransaction = [];

    return {
      theme,headers,headersCompte,headersTransaction,itemsTransaction,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
      toast
    }
  },
  components: {
    ChequeRequest,
    //searchBar,
    EasyDataTable: Vue3EasyDataTable,AccountCreationModal
  },
  data () {
    return {
      itemsCompte : [],
      itemsUser: [],
      itemsUserTable : [],
      itemsCompteTable : [],
      itemsTransactionTable : this.itemsTransaction,
      drawer: true,
      selectedUser: null,
      selectedCompte: null,
      rail: true,
      dialogDeleteUser: false,
      dialogDeleteCompte: false,
      loadingTransaction: false,

      dialogCreatUser: false,

    }
  },
  methods: {
    can,
    //USER
    //permet de choisir un utilisateur
    selectUser(item){
      this.$data.selectedUser = item;
      this.$data.itemsCompteTable = this.itemsCompte.filter((i) => i.id_owner === item.userId);
      //console.table(item);
    },

    //supprime un utilisateur
    //TODO requete back pour suppression user
    async deleteUser(user) {
      //TODO FETCH REQUEST
      //check if user is null
      //probably need to inform user if nothing append but for the moment.. FUCK IT
      if(user === null) return;


      const config = {
        method: 'DELETE',
        headers: authHeader()
      }
      //config ready
      try {
        //TODO need the api path for delete a user
        const response = await fetch(`/api/user/delete/${user.userId}`,config);
        const { results: data } = await response.json()
        //console.log(data)

        this.toast.success(`Utilisateur bien supprimer, ${data}`)
      }catch (error){
        //console.log(error.message);
        this.toast.error(error.message)

      }
    },

    //TODO selectCompte permet de selection un compte et fait un appel a l'api pour récuperer les transactions du compte
    selectCompte(compte){
      if(this.selectedCompte!==null && this.selectedCompte.id === compte.id){
        return;
      }
      //console.log(compte);
      this.selectedCompte = compte;
      //call to the api to get transaction about this account
      this.GetTransactions(compte)

    },

    //supprime un Compte
    //TODO requete back pour suppression Compte
    async deleteCompte(compte) {
      //TODO FETCH REQUEST
      //check if compte is null
      //probably need to inform user if nothing append but for the moment.. FUCK IT
      if(compte === null) return;

      //prepare Fetch config
      const config = {
        method: 'DELETE',
        headers: authHeader()
      }
      //config readyid_compte
      try {
        //TODO need the api path for delete an account
        const response = await fetch(`/api/account/delete/${compte.id_owner}/${compte.id}`,config);
        await response.json()
        //console.log(data)
        this.toast.success(`Suppression du compte réussi`)
        this.dialogDeleteCompte = false;
        this.getComptes();

      }catch (error){
        //console.log(error);
        this.toast.error(error.message)
      }

      //TODO remove this part
      //console.log(compte)
    },

    async GetTransactions(compte) {
      //TODO FETCH REQUEST
      //check if compte is null
      //probably need to inform user if nothing append but for the moment.. FUCK IT
      if(compte === null) return;

      //prepare Fetch config
      const config = {
        method: 'GET',
        headers: authHeader()
      }
      //config ready
      try {
        this.$data.loadingTransaction = true;
        //TODO need the api path for get transaction from an account
        const response = await fetch(`/api/transactions/${compte.id}`,config);
        //const { results: data } = await response.json()
        await response.json()
        //add transaction to the tableTransaction
      }catch (error){
        //console.log(error);
        //this.toast.error(error.message)
      }

      this.$data.itemsTransactionTable = [
        { id_transaction: "transaction-0001",date: "2021-11-28", source: "compte_4",destination: "compte_1", amount: 100},
        { id_transaction: "transaction-0002",date: "2021-11-28", source: "compte_2",destination: "compte_1", amount: 200},
        { id_transaction: "transaction-0003", date: "2021-11-28",source: "compte_3",destination: "compte_1", amount: 300},
        { id_transaction: "transaction-0004", date: "2021-11-28",source: "compte_4",destination: "compte_1", amount: 8700},
        { id_transaction: "transaction-0005", date: "2021-11-28",source: "compte_3",destination: "compte_1", amount: 58},
        { id_transaction: "transaction-0006", date: "2021-11-28",source: "compte_4",destination: "compte_1", amount: 42},
        { id_transaction: "transaction-0007", date: "2021-11-28",source: "compte_3",destination: "compte_1", amount: 789},
        { id_transaction: "transaction-0008", date: "2021-11-28",source: "compte_4",destination: "compte_1", amount: 99},
      ];

      this.$data.loadingTransaction=false;
      //TODO remove this part
      //console.log(compte)
    },


    //reset les choix et enleve les filtres
    resetSelectedRows(){
      //user
      this.$data.selectedUser = null;
      this.$data.itemsCompteTable = this.itemsCompte;

      //transaction
      this.$data.selectedCompte = null;
      this.$data.itemsTransactionTable = [];

    },

    openModalDeleteUser(user){
      this.$data.selectedUser = user;
      this.$data.dialogDeleteUser = true;
    },

    openModalDeleteCompte(compte){
      this.$data.selectedCompte = compte;
      this.$data.dialogDeleteCompte = true;
    },

    async getComptes(){

      const user_connect = this.$store.state.auth.user.id;


  // //prepare Fetch config
  // const config = {
  //   method: 'GET',
  //   headers: {
  //     'Authorization': `Bearer ${this.$store.state.token}`
  //   }
  // }
  //config ready
  try {

    let uri = `/api/accounts/${user_connect}`;

    if(can('advisor')){
      uri = `/api/accounts/attached/${user_connect}`;
    }

    const response = await fetch(uri,{
      headers: authHeader()
    });
    const data = await response.json()
    //console.log(results)
    this.$data.itemsCompte = data;
    this.$data.itemsCompteTable = this.$data.itemsCompte;
  }catch (error){
    //console.log(error);
    this.toast.error(error.message)

  }
  },

    async getUsers(){

      const user_connect = this.$store.state.auth.user.id;

      try {
        //TODO need the api path for delete a user
        const response = await fetch(`/api/user/all/${user_connect}`,{
          headers: authHeader()
        });
        const results = await response.json()
        //console.log(results)
        this.$data.itemsUser = results;
        this.$data.itemsUserTable = this.$data.itemsUser;
      }catch (error){
       //console.log(error);
        this.toast.error(error.message)
      }
    }

  },
  beforeMount() {
    this.getComptes()
    this.getUsers()
  }
}
</script>

<style>


.customize-table {
  --easy-table-border: 1px solid #445269;
  --easy-table-row-border: 1px solid #445269;

  --easy-table-header-font-size: 14px;
  --easy-table-header-height: 50px;
  --easy-table-header-font-color: #c1cad4;
  --easy-table-header-background-color: #2d3a4f;

  --easy-table-header-item-padding: 10px 15px;

  --easy-table-body-even-row-font-color: #fff;
  --easy-table-body-even-row-background-color: #4c5d7a;

  --easy-table-body-row-font-color: #c0c7d2;
  --easy-table-body-row-background-color: #2d3a4f;
  --easy-table-body-row-height: 50px;
  --easy-table-body-row-font-size: 14px;

  --easy-table-body-row-hover-font-color: #2d3a4f;
  --easy-table-body-row-hover-background-color: #eee;

  --easy-table-body-item-padding: 10px 15px;

  --easy-table-footer-background-color: #2d3a4f;
  --easy-table-footer-font-color: #c0c7d2;
  --easy-table-footer-font-size: 14px;
  --easy-table-footer-padding: 0px 10px;
  --easy-table-footer-height: 50px;

  --easy-table-rows-per-page-selector-width: 70px;
  --easy-table-rows-per-page-selector-option-padding: 10px;
  --easy-table-rows-per-page-selector-z-index: 1;


  --easy-table-scrollbar-track-color: #2d3a4f;
  --easy-table-scrollbar-color: #2d3a4f;
  --easy-table-scrollbar-thumb-color: #4c5d7a;;
  --easy-table-scrollbar-corner-color: #2d3a4f;

  --easy-table-loading-mask-background-color: #2d3a4f;
}
</style>